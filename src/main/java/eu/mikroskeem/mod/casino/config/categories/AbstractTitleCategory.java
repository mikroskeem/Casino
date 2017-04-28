package eu.mikroskeem.mod.casino.config.categories;

import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * @author Mark Vainomaa
 */
@ConfigSerializable
@Getter
public abstract class AbstractTitleCategory {
    @Setting int fadeIn = 20;
    @Setting int stay = 40;
    @Setting int fadeOut = 20;
    @Setting String title;
    @Setting String subtitle;
}
