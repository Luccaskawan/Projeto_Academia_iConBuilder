package br.univille.projfso2024b.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.univille.projfso2024b.entity.Agendamento;
import br.univille.projfso2024b.entity.Cliente;
import br.univille.projfso2024b.repository.AgendamentoRepository;
import br.univille.projfso2024b.repository.ClienteRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class AgendamentoController {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/agendamentos")
    public ModelAndView listarAgendamentos(HttpSession session) {
        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId == null) {
            return new ModelAndView("redirect:/login");
        }

        Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
        if (cliente == null) {
            return new ModelAndView("redirect:/login");
        }

        List<Agendamento> agendamentos = agendamentoRepository.findByCliente(cliente);
        return new ModelAndView("agendamentos", "agendamentos", agendamentos);
    }

    @PostMapping("/agendar")
    public ModelAndView agendar(
            @RequestParam("descricao") String descricao,
            @RequestParam("dataHora") String dataHoraStr,
            HttpSession session) {
        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId == null) {
            return new ModelAndView("redirect:/login");
        }

        Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
        if (cliente == null) {
            return new ModelAndView("redirect:/login");
        }

        Date dataHora;
        try {
            dataHora = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(dataHoraStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new ModelAndView("agendamentos", "error", "Data e hora inválidos");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setDataHora(dataHora);
        agendamento.setDescricao(descricao);

        agendamentoRepository.save(agendamento);

        return new ModelAndView("redirect:/agendamentos");
    }

    @PostMapping("/agendamentos/delete")
    public String deleteAgendamento(@RequestParam("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Long clienteId = (Long) session.getAttribute("clienteId");
        if (clienteId == null) {
            return "redirect:/login";
        }

        Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
        if (cliente == null) {
            return "redirect:/login";
        }

        Agendamento agendamento = agendamentoRepository.findById(id).orElse(null);
        if (agendamento != null && agendamento.getCliente().equals(cliente)) {
            agendamentoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Agendamento excluído com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Agendamento não encontrado ou não pertence ao cliente.");
        }

        return "redirect:/agendamentos";
    }
}
