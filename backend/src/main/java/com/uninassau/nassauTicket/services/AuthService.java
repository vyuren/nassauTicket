package com.uninassau.nassauTicket.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.uninassau.nassauTicket.dto.AuthLoginRequest;
import com.uninassau.nassauTicket.dto.CadastroRequest;
import com.uninassau.nassauTicket.dto.clienteLogin;
import com.uninassau.nassauTicket.entities.LoginSessionEntity;
import com.uninassau.nassauTicket.entities.atendenteEntity;
import com.uninassau.nassauTicket.entities.clienteEntity;
import com.uninassau.nassauTicket.repositories.atendenteRepository;
import com.uninassau.nassauTicket.repositories.clienteRepository;
import com.uninassau.nassauTicket.repositories.loginSessionRepository;

@Service
public class AuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final clienteRepository clienteRepository;
    private final atendenteRepository atendenteRepository;
    private final loginSessionRepository loginSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final clienteService clienteService;

    public AuthService(
        clienteRepository clienteRepository,
        atendenteRepository atendenteRepository,
        loginSessionRepository loginSessionRepository,
        PasswordEncoder passwordEncoder,
        clienteService clienteService
    ){
        this.clienteRepository = clienteRepository;
        this.atendenteRepository = atendenteRepository;
        this.loginSessionRepository = loginSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.clienteService = clienteService;
    }

    @Transactional
    public SessionResult registerClient(CadastroRequest cadastro){
        String cpf = normalizeCpf(cadastro.cpf());
        if (clienteRepository.findByEmail(cadastro.email()).isPresent()
            || clienteRepository.existsByCpf(cpf)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail ou CPF já cadastrado.");
        }

        clienteEntity cliente = clienteService.CriarCliente(
            cadastro.nome(),
            cadastro.nascimento(),
            cpf,
            cadastro.email(),
            cadastro.senha()
        );
        return createSession(new AuthenticatedUser(cliente.GetId(), cliente.GetNome(), "CLIENTE"));
    }

    @Transactional
    public SessionResult registerAttendant(CadastroRequest cadastro){
        String cpf = normalizeCpf(cadastro.cpf());
        if (atendenteRepository.findByEmail(cadastro.email()).isPresent()
            || atendenteRepository.existsByCpf(cpf)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail ou CPF já cadastrado.");
        }

        atendenteEntity atendente = new atendenteEntity();
        atendente.SetNome(cadastro.nome());
        atendente.SetNascimento(cadastro.nascimento());
        atendente.SetCpf(cpf);
        atendente.SetEmail(cadastro.email());
        atendente.SetSenhaHash(passwordEncoder.encode(cadastro.senha()));
        atendente = atendenteRepository.save(atendente);

        return createSession(new AuthenticatedUser(atendente.GetId(), atendente.GetNome(), "ATENDENTE"));
    }

    @Transactional
    public SessionResult loginClient(clienteLogin login){
        clienteEntity cliente = clienteRepository.findByEmail(login.email())
            .filter(account -> account.GetSenhaHash() != null
                && passwordEncoder.matches(login.senha(), account.GetSenhaHash()))
            .orElseThrow(AuthService::invalidCredentials);
        return createSession(new AuthenticatedUser(cliente.GetId(), cliente.GetNome(), "CLIENTE"));
    }

    @Transactional
    public SessionResult loginAttendant(AuthLoginRequest login){
        atendenteEntity atendente = atendenteRepository.findByEmail(login.email())
            .filter(account -> account.GetSenhaHash() != null
                && passwordEncoder.matches(login.senha(), account.GetSenhaHash()))
            .orElseThrow(AuthService::invalidCredentials);
        return createSession(new AuthenticatedUser(atendente.GetId(), atendente.GetNome(), "ATENDENTE"));
    }

    @Transactional
    public Optional<AuthenticatedUser> findSession(String rawToken){
        if (rawToken == null || rawToken.isBlank()){
            return Optional.empty();
        }

        Instant now = Instant.now();
        return loginSessionRepository.findByTokenHashAndExpiracaoAfter(hashToken(rawToken), now)
            .flatMap(session -> findUser(session.getPerfil(), session.getUsuarioId()));
    }

    @Transactional
    public void logout(String rawToken){
        if (rawToken != null && !rawToken.isBlank()){
            loginSessionRepository.deleteById(hashToken(rawToken));
        }
    }

    private Optional<AuthenticatedUser> findUser(String perfil, Long id){
        if ("CLIENTE".equals(perfil)){
            return clienteRepository.findById(id)
                .map(cliente -> new AuthenticatedUser(cliente.GetId(), cliente.GetNome(), "CLIENTE"));
        }
        if ("ATENDENTE".equals(perfil)){
            return atendenteRepository.findById(id)
                .map(atendente -> new AuthenticatedUser(atendente.GetId(), atendente.GetNome(), "ATENDENTE"));
        }
        return Optional.empty();
    }

    private SessionResult createSession(AuthenticatedUser user){
        byte[] randomToken = new byte[32];
        SECURE_RANDOM.nextBytes(randomToken);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomToken);
        Instant now = Instant.now();
        loginSessionRepository.deleteByExpiracaoBefore(now);
        loginSessionRepository.save(new LoginSessionEntity(
            hashToken(token),
            user.id(),
            user.perfil(),
            now.plus(SessionCookie.LIFETIME)
        ));
        return new SessionResult(token, user);
    }

    private static String hashToken(String token){
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException exception){
            throw new IllegalStateException("SHA-256 não está disponível.", exception);
        }
    }

    private static ResponseStatusException invalidCredentials(){
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
    }

    private static String normalizeCpf(String cpf){
        String normalized = cpf.replaceAll("\\D", "");
        if (normalized.length() != 11){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF deve conter 11 dígitos.");
        }
        return normalized;
    }

    public record AuthenticatedUser(Long id, String nome, String perfil){
    }

    public record SessionResult(String token, AuthenticatedUser user){
    }
}
