package sn.isi.immobilier.service.impl;


import sn.isi.immobilier.service.AdminService;
import sn.isi.immobilier.dao.*;
import sn.isi.immobilier.dao.impl.*;

import java.util.HashMap;
import java.util.Map;

public class AdminServiceImpl implements AdminService {
    private final UserDao userDAO = new UserDaoImpl();
    private final BuildingDao buildingDAO = new BuildingDaoImpl();
    private final UnitDao unitDAO = new UnitDaoImpl();
    private final LeaseDao leaseDAO = new LeaseDaoImpl();
    private final PaymentDao paymentDAO = new PaymentDaoImpl();

    @Override
    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", (long) userDAO.findAll().size());
        stats.put("totalBuildings", (long) buildingDAO.findAll().size());
        stats.put("totalUnits", (long) unitDAO.findAll().size());
        stats.put("totalLeases", (long) leaseDAO.findAll().size());
        stats.put("totalPayments", (long) paymentDAO.findAll().size());
        return stats;
    }
}
