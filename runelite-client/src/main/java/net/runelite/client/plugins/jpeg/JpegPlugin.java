package net.runelite.client.plugins.jpeg;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.icafe4j.image.ImageIO;
import com.icafe4j.image.ImageParam;
import com.icafe4j.image.ImageType;
import com.icafe4j.image.quant.QuantQuality;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.DrawManager;

@Slf4j
@PluginDescriptor(
	name = "Jpeg me",
	enabledByDefault = false
)
public class JpegPlugin extends Plugin
{
	@Inject
	private DrawManager drawManager;

	private JpegListener jpegListener;

	public JpegPlugin() {
		jpegListener = new JpegListener(this);
	}

	@Provides
	JpegConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(JpegConfig.class);
	}

	@Inject
	private JpegConfig config;

	@Override
	protected void startUp() throws Exception
	{
		drawManager.registerEveryFrameListener(jpegListener);
	}

	@Override
	protected void shutDown() throws Exception
	{
		drawManager.unregisterEveryFrameListener(jpegListener);
	}

	private static class JpegListener implements Consumer<Image> {
		final JpegPlugin plugin;
		final ImageParam param;
		JpegListener(JpegPlugin plugin) {
			this.plugin = plugin;
			param = ImageParam.getBuilder()
				.quantQuanlity(QuantQuality.POOR)
				.build();
		}
		@Override
		public void accept(Image image)
		{
			if (plugin.config.repititions() == 0) {
				return;
			}

			try {
				BufferedImage workingImage = toBufferedImage(image);

				for (int i = 0; i < plugin.config.repititions(); i++) {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					ImageIO.write(workingImage, outputStream, ImageType.JPG, param);
					InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
					workingImage = ImageIO.read(inputStream);
				}

				image.getGraphics().drawImage(workingImage, 0, 0, null);
			} catch (Exception e) {
				log.error("I DUNNO", e);
			}
		}

		public BufferedImage toBufferedImage(Image img)
		{
			if (img instanceof BufferedImage)
			{
				return (BufferedImage) img;
			}

			// Create a buffered image with transparency
			BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

			// Draw the image on to the buffered image
			Graphics2D bGr = bimage.createGraphics();
			bGr.drawImage(img, 0, 0, null);
			bGr.dispose();

			// Return the buffered image
			return bimage;
		}
	}
}
