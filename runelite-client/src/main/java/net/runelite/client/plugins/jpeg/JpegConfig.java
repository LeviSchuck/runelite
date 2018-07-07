package net.runelite.client.plugins.jpeg;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(
	keyName = "jpeg",
	name = "Jpeg me",
	description = "Lets you control the things"
)
public interface JpegConfig extends Config
{
	@ConfigItem(
		keyName = "limitMode",
		name = "Limit Mode",
		description = "Stay at or under the target frames per second even when in this mode",
		position = 1
	)
	default int repititions()
	{
		return 0;
	}
}
