package edu.univ.erp.api.maintenance;

import edu.univ.erp.service.AdminService;

/**
 * API for maintenance mode operations
 */
public class MaintenanceApi {
    private final AdminService adminService;

    public MaintenanceApi() {
        this.adminService = new AdminService();
    }

    /**
     * Check if maintenance mode is enabled
     */
    public boolean isMaintenanceMode() {
        return adminService.isMaintenanceMode();
    }

    /**
     * Toggle maintenance mode
     */
    public AdminService.MaintenanceModeResult toggleMaintenanceMode(boolean enabled) {
        return adminService.toggleMaintenanceMode(enabled);
    }
}
