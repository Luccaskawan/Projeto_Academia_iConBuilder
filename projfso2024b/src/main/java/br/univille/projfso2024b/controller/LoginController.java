package br.univille.projfso2024b.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.univille.projfso2024b.entity.Cliente;
import br.univille.projfso2024b.repository.ClienteRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @PostMapping("/login")
    public ModelAndView authenticate(
            @RequestParam("email") String email,
            @RequestParam("senha") String senha,
            HttpSession session) {

        // Usando Optional para buscar o cliente pelo email
        var clienteOptional = clienteRepository.findByEmail(email);

        // Verificando se o cliente foi encontrado e a senha está correta
        if (clienteOptional.isPresent() && clienteOptional.get().getSenha().equals(senha)) {
            Cliente cliente = clienteOptional.get();
            session.setAttribute("clienteId", cliente.getId());
            session.setAttribute("clienteNome", cliente.getNome());  // Adicionando o nome do cliente na sessão
            return new ModelAndView("redirect:/agendamentos");
        } else {
            return new ModelAndView("login", "error", "Email ou senha inválidos");
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
