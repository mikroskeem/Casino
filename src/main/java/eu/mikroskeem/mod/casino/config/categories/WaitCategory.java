package eu.mikroskeem.mod.casino.config.categories;

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * @author Mark Vainomaa
 */
@ConfigSerializable
public class WaitCategory extends AbstractTitleCategory {
    public WaitCategory() {
        fadeIn = 2;
        stay = 40;
        fadeOut = 5;
        title = "&9Casino";
        subtitle = "&bWait &e%seconds%&b seconds...";
    }
}
