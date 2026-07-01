package com.paska.agritech.controller;

import com.paska.agritech.dao.ParcelaDAO;
import com.paska.agritech.dao.SensorDAO;
import com.paska.agritech.model.Parcela;
import com.paska.agritech.model.Sensor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * CONTROLADOR de Monitoreo (capa Controller del MVC).
 * Muestra el inventario de parcelas y sensores leido desde PostgreSQL,
 * resaltando los sensores con bateria critica.
 */
@Controller
public class MonitoreoController {

    private final ParcelaDAO parcelaDAO = new ParcelaDAO();
    private final SensorDAO sensorDAO = new SensorDAO();

    @GetMapping("/monitoreo")
    public String monitoreo(Model model) {
        List<Parcela> parcelas = parcelaDAO.listar();
        List<Sensor> sensores = sensorDAO.listar();
        List<Sensor> bateriaBaja = sensorDAO.listarBateriaBaja(50.0);

        model.addAttribute("parcelas", parcelas);
        model.addAttribute("sensores", sensores);
        model.addAttribute("bateriaBaja", bateriaBaja);
        model.addAttribute("totalParcelas", parcelas.size());
        model.addAttribute("totalSensores", sensores.size());
        model.addAttribute("totalAlertas", bateriaBaja.size());
        return "monitoreo";
    }
}
