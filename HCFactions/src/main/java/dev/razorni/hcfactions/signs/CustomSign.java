package dev.razorni.hcfactions.signs;

import dev.razorni.hcfactions.extras.framework.Module;
import lombok.Getter;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public abstract class CustomSign extends Module<CustomSignManager> {
    protected List<String> lines;

    public CustomSign(CustomSignManager manager, List<String> lines) {
        super(manager);
        this.lines = lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public abstract void onClick(Player player, Sign sign);

    public Integer getIndex(String input) {
        return this.lines.indexOf(this.lines.stream().filter(s -> s.toLowerCase().contains(input)).findFirst().orElse(null));
    }
}
