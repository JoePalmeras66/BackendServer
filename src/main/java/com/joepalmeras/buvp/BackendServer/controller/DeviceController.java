package com.joepalmeras.buvp.BackendServer.controller;

import com.joepalmeras.buvp.BackendServer.domain.Device;
import com.joepalmeras.buvp.BackendServer.exception.ResourceNotFoundException;
import com.joepalmeras.buvp.BackendServer.service.DeviceService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DeviceController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int ROW_PER_PAGE = 5;

    @Autowired
    private DeviceService deviceService;

    @Value("${msg.title}")
    private String title;

    @GetMapping(value = {"/", "/index"})
    public String index(Model model) {
        model.addAttribute("title", title);
        return "index";
    }

    @GetMapping(value = "/devices")
    public String getDevices(Model model,
            	@RequestParam(value = "page", defaultValue = "1") int pageNumber) {
        List<Device> devices = deviceService.findAll(pageNumber, ROW_PER_PAGE);

        long count = deviceService.count();
        boolean hasPrev = pageNumber > 1;
        boolean hasNext = (pageNumber * ROW_PER_PAGE) < count;
        model.addAttribute("devices", devices);
        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("prev", pageNumber - 1);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("next", pageNumber + 1);
        return "device-list";
    }

    @GetMapping(value = "/devices/{deviceId}")
    public String getDeviceById(Model model, @PathVariable long deviceId) {
        Device device = null;
        try {
            device = deviceService.findById(deviceId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Device not found");
        }
        model.addAttribute("device", device);
        return "device";
    }

    @GetMapping(value = {"/devices/add"})
    public String showAddDevice(Model model) {
        Device device = new Device();
        model.addAttribute("add", true);
        model.addAttribute("device", device);

        return "device-edit";
    }

    @PostMapping(value = "/devices/add")
    public String addDevice(Model model,
            @ModelAttribute("device") Device device) {        
        try {
            Device newDevice = deviceService.save(device);
            return "redirect:/devices/" + String.valueOf(newDevice.getId());
        } catch (Exception ex) {
            
        	String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);

            model.addAttribute("add", true);
            return "device-edit";
        }        
    }

    @GetMapping(value = {"/devices/{deviceId}/edit"})
    public String showEditDevice(Model model, @PathVariable long deviceId) {
        Device device = null;
        try {
            device = deviceService.findById(deviceId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Device not found");
        }
        model.addAttribute("add", false);
        model.addAttribute("device", device);
        return "device-edit";
    }

    @PostMapping(value = {"/devices/{deviceId}/edit"})
    public String updateDevice(Model model,
            @PathVariable long deviceId,
            @ModelAttribute("device") Device device) {        
        try {
            device.setId(deviceId);
            deviceService.update(device);
            return "redirect:/devices/" + String.valueOf(device.getId());
        } catch (Exception ex) {
            
            String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);

             model.addAttribute("add", false);
            return "device-edit";
        }
    }

    @GetMapping(value = {"/devices/{deviceId}/delete"})
    public String showDeleteDeviceById(
            Model model, @PathVariable long deviceId) {
        Device device = null;
        try {
            device = deviceService.findById(deviceId);
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("errorMessage", "Device not found");
        }
        model.addAttribute("allowDelete", true);
        model.addAttribute("device", device);
        return "device";
    }

    @PostMapping(value = {"/devices/{deviceId}/delete"})
    public String deleteDeviceById(
            Model model, @PathVariable long deviceId) {
        try {
            deviceService.deleteById(deviceId);
            return "redirect:/devices";
        } catch (ResourceNotFoundException ex) {
            String errorMessage = ex.getMessage();
            logger.error(errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            return "device";
        }
    }
    
}
