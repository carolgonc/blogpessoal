package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new Usuario(null, "Root", "rootroot", "root@root.com", " "));
	}
	
	@Test
	@DisplayName("Cadastrar um usuario")
	public void deveCriarUmUsuario() {
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(null, "Thiago", "123456789", "thiago@email.com", " "));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Não deve permitir duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(null, "Maria da Silva", "12345678" , "maria_silva@email.com.br" , " "));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(null, "Maria da Silva", "12345678" , "maria_silva@email.com.br", " "));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(null, "Juliana Andrews", "juliana123" , "juliana_ramos@email.com", " "));
		
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), "Juliana Andrews Ramos", "juliana123" , "juliana_ramos@email.com", " " );
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario(null, "Sabrina Sanches", "sabrina123", "sabrina_sanches@email.com", " "));
		
		usuarioService.cadastrarUsuario(new Usuario(null, "Ricardo Marques", "ricardo123", "ricardo_marques@email.com", " "));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
}


