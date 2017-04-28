package eu.mikroskeem.mod.casino.config.categories;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * @author Mark Vainomaa
 */
@ConfigSerializable
@Getter
public class LoseCategory extends AbstractTitleCategory {
    public LoseCategory() {
        fadeIn = 5;
        stay = 60;
        fadeOut = 5;
        title = "&9Casino";
        subtitle = "&cSorry, you have lost &4%amount%&c$";
    }
}
