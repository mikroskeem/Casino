package eu.mikroskeem.mod.casino.config.categories;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * @author Mark Vainomaa
 */
@ConfigSerializable
@Getter
public class WinCategory extends AbstractTitleCategory {
    public WinCategory() {
        fadeIn = 5;
        stay = 60;
        fadeOut = 5;
        title = "&9Casino";
        subtitle = "&aCongratulations, you have won &e%amount%$&b.";
    }
}
