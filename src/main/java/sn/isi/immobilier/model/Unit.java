package sn.isi.immobilier.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import sn.isi.immobilier.model.Enums.UnitStatus;

import java.math.BigDecimal;
import java.time.Instant;


@Entity @Table(
        name="unit",
        uniqueConstraints = @UniqueConstraint(name="uq_unit_per_building", columnNames={"building_id","unit_number"})
)
public class Unit {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="building_id")
    private Building building;

    @Column(name="unit_number", nullable=false, length=50) private String unitNumber;
    @Column(nullable=false) private int rooms;
    @Column(name="area_m2", precision=10, scale=2) private java.math.BigDecimal areaM2;
    private Integer floor;

    @Column(name="rent_amount", nullable=false, precision=12, scale=2)
    private java.math.BigDecimal rentAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private UnitStatus status = UnitStatus.AVAILABLE;

    @Column(columnDefinition="TEXT") private String features;

    public Unit() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }

    public String getUnitNumber() { return unitNumber; }
    public void setUnitNumber(String unitNumber) { this.unitNumber = unitNumber; }

    public int getRooms() { return rooms; }
    public void setRooms(int rooms) { this.rooms = rooms; }

    public BigDecimal getAreaM2() { return areaM2; }
    public void setAreaM2(BigDecimal areaM2) { this.areaM2 = areaM2; }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public BigDecimal getRentAmount() { return rentAmount; }
    public void setRentAmount(BigDecimal rentAmount) { this.rentAmount = rentAmount; }

    public UnitStatus getStatus() { return status; }
    public void setStatus(UnitStatus status) { this.status = status; }

    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }
}

