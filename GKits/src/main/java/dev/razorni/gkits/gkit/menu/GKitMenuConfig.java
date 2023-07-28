package dev.razorni.gkits.gkit.menu;

import dev.razorni.gkits.GKits;
import cc.invictusgames.ilib.configuration.StaticConfiguration;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class GKitMenuConfig implements StaticConfiguration {

    private List<GKitMenuItem> kitMenuItemList
            = new ArrayList<>();

    public void saveConfig() {
        try {
            GKits.get().getConfigurationService().saveConfiguration(this,
                    new File(GKits.get().getDataFolder(), "menu.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
