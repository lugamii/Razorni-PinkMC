package eu.vortexdev.invictusspigot.config;

import java.util.List;

public enum PearlConfig {
    CROSSPEARLMOVEHELPER(0.3, "pearls.crosspearl.extra-move-helper"),

    TALITELEPORTY(0.5, "pearls.tali.teleportY"),
    TALITELEPORT(false, "pearls.tali.enabled"),

    FIXWALLGLITCH(true, "pearls.anti-glitch.fix-walls-glitch"),
    CHESTFENCEENABLED(false, "pearls.anti-glitch.chest-under-fencegate-enabled"),
    HOPPERFENCENABLED(false, "pearls.anti-glitch.hopper-under-fencegate-enabled"),
    FIXFENCEGATEGLITCH(true, "pearls.anti-glitch.fix-fence-gate-glitch"),

    REFUNDIFSUFFOCATING(true, "pearls.prevent-suffocation.refund-if-suffocation"),
    REFUNDRISKYPEARL(true, "pearls.prevent-suffocation.refund-risky-pearl"),
    REFUNDIFSOCLOSE(true, "pearls.prevent-suffocation.refund-if-so-close"),
    REFUNDIFSOCLOSEDISTANCE(1.0, "pearls.prevent-suffocation.refund-if-so-close-distance"),

    BETTERHITDETECTION(true, "pearls.entity-teleport.better-hit-detection"),
    GETOUTFROMONEBYONE(false, "pearls.entity-teleport.get-out-from-one-by-one"),

    REFUNDONCRITBLOCK(false, "pearls.thru.refund-on-critblock"),
    MAXPEARLTHRUPASSBLOCKS(1, "pearls.thru.max-pearl-pass-thru-blocks"),
    INSTANTLYPASTHRU(false, "pearls.thru.instantly-pass-thru"),
    HITTHRUBLOCK(true, "pearls.thru.hit-thru-block"),
    PRETHRUBLOCK(true, "pearls.thru.pre-thru-block"),
    THRUFENCEGATE(true, "pearls.thru.fence-gates"),
    THRUCOBWEB(true, "pearls.thru.cobweb"),
    THRUSTRING(true, "pearls.thru.string"),
    THRUPLANTS(false, "pearls.thru.plants"),

    SLABS(true, ""), 
    SLABS_ENABLE(true, "pearls.thru.slabs.enabled"),
    SLABS_DIAGONAL(true, "pearls.thru.slabs.diagonal"),
    SLABS_CRITBLOCK(true, "pearls.thru.slabs.critblock"),
    SLABS_CROSSPEARL(true, "pearls.thru.slabs.crosspearl"),

    STAIRS(true, ""), 
    STAIRS_ENABLE(true, "pearls.thru.stairs.enabled"),
    STAIRS_DIAGONAL(true, "pearls.thru.stairs.diagonal"),
    STAIRS_CRITBLOCK(true, "pearls.thru.stairs.critblock"),
    STAIRS_CROSSPEARL(true, "pearls.thru.stairs.crosspearl"),

    CHESTS(true, ""), 
    CHESTS_ENABLE(true, "pearls.thru.chests.enabled"),
    CHESTS_DIAGONAL(false, "pearls.thru.chests.diagonal"),
    CHESTS_CRITBLOCK(false, "pearls.thru.chests.critblock"),
    CHESTS_CROSSPEARL(false, "pearls.thru.chests.crosspearl"),

    BED(true, ""), 
    BED_ENABLE(true, "pearls.thru.bed.enabled"),
    BED_DIAGONAL(false, "pearls.thru.bed.diagonal"),
    BED_CRITBLOCK(false, "pearls.thru.bed.critblock"),
    BED_CROSSPEARL(false, "pearls.thru.bed.crosspearl"),

    COBBLEWALL(true, ""), 
    COBBLEWALL_ENABLE(true, "pearls.thru.cobble-wall.enabled"),
    COBBLEWALL_DIAGONAL(false, "pearls.thru.cobble-wall.diagonal"),
    COBBLEWALL_CRITBLOCK(false, "pearls.thru.cobble-wall.critblock"),
    COBBLEWALL_CROSSPEARL(false, "pearls.thru.cobble-wall.crosspearl"),

    PISTONS(true, ""), 
    PISTONS_ENABLE(true, "pearls.thru.pistons.enabled"),
    PISTONS_DIAGONAL(false, "pearls.thru.pistons.diagonal"),
    PISTONS_CRITBLOCK(false, "pearls.thru.pistons.critblock"),
    PISTONS_CROSSPEARL(false, "pearls.thru.pistons.crosspearl"),

    PORTALFRAME(true, ""), 
    PORTALFRAME_ENABLE(true, "pearls.thru.portal-frame.enabled"),
    PORTALFRAME_DIAGONAL(false, "pearls.thru.portal-frame.diagonal"),
    PORTALFRAME_CRITBLOCK(false, "pearls.thru.portal-frame.critblock"),
    PORTALFRAME_CROSSPEARL(false, "pearls.thru.portal-frame.crosspearl"),

    ENCHANTTABLE(true, ""), 
    ENCHANTTABLE_ENABLE(true, "pearls.thru.enchant-table.enabled"),
    ENCHANTTABLE_DIAGONAL(false, "pearls.thru.enchant-table.diagonal"),
    ENCHANTTABLE_CRITBLOCK(false, "pearls.thru.enchant-table.critblock"),
    ENCHANTTABLE_CROSSPEARL(false, "pearls.thru.enchant-table.crosspearl"),

    ANVIL(true, ""), 
    ANVIL_ENABLE(true, "pearls.thru.anvil.enabled"),
    ANVIL_DIAGONAL(false, "pearls.thru.anvil.diagonal"),
    ANVIL_CRITBLOCK(false, "pearls.thru.anvil.critblock"),
    ANVIL_CROSSPEARL(false, "pearls.thru.anvil.crosspearl"),

    DAYLIGHTSENSOR(true, ""), 
    DAYLIGHTSENSOR_ENABLE(true, "pearls.thru.day-light-sensor.enabled"),
    DAYLIGHTSENSOR_DIAGONAL(false, "pearls.thru.day-light-sensor.diagonal"),
    DAYLIGHTSENSOR_CRITBLOCK(false, "pearls.thru.day-light-sensor.critblock"),
    DAYLIGHTSENSOR_CROSSPEARL(false, "pearls.thru.day-light-sensor.crosspearl"),

    TRAPDOOR(true, ""), 
    TRAPDOOR_ENABLE(true, "pearls.thru.trap-door.enabled"),
    TRAPDOOR_DIAGONAL(false, "pearls.thru.trap-door.diagonal"),
    TRAPDOOR_CRITBLOCK(false, "pearls.thru.trap-door.critblock"),
    TRAPDOOR_CROSSPEARL(false, "pearls.thru.trap-door.crosspearl"),

    PEARLDAMAGE(5.0, "pearls.other.damage"),
    ONGLITCHRETURNPEARL(true, "pearls.other.refund-return-pearl"),
    ONGLITCHMESSAGE("&7(&d&l!&7) &fYour pearl was refunded!", "pearls.other.refund-message"),
    ONGLITCHCONSOLECOMMAND("", "pearls.other.refund-console-command");

    private final String configSection;
    private Object value;

    PearlConfig(Object value, String configSection) {
        this.value = value;
        this.configSection = configSection;
    }

    public boolean getBooleanValue() {
        return (Boolean) value;
    }

    public String getConfigSection() {
        return configSection;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public double getDoubleValue() {
        if (value instanceof Integer)
            return (Integer) value;
        return (Double) value;
    }

    public List<?> getListValue() {
        return (List<?>) value;
    }

    public boolean isCritblock() {
        return valueOf(name() + "_CRITBLOCK").getBooleanValue();
    }

    public boolean isDiagonal() {
        return valueOf(name() + "_DIAGONAL").getBooleanValue();
    }

    public boolean isEnabled() {
        PearlConfig set = valueOf(name() + "_ENABLE");
        return set.getBooleanValue();
    }

    public boolean isCrosspearl() {
        return valueOf(name() + "_CROSSPEARL").getBooleanValue();
    }

    public int getIntValue() {
        if (value instanceof Double)
            return ((Double) value).intValue();
        return (Integer) value;
    }

    public String getStringValue() {
        return (String) value;
    }

}
