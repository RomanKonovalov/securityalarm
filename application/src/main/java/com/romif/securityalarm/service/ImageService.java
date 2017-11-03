package com.romif.securityalarm.service;

import com.romif.securityalarm.config.ApplicationProperties;
import com.romif.securityalarm.domain.Image;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ImageService {

    private static final ImageObserver DUMMY_OBSERVER = (img, infoflags, x, y, width, height) -> true;

    @Inject
    private ApplicationProperties applicationProperties;

    public byte[] getThumbnail(List<Image> images) throws IOException {

        if (CollectionUtils.isEmpty(images)) {
            return null;
        }

        BufferedImage imgIn = ImageIO.read(new ByteArrayInputStream(images.get(0).getRawImage()));

        double scale;
        if (imgIn.getWidth() >= imgIn.getHeight()) {
            // horizontal or square image
            scale = Math.min(applicationProperties.getImage().getMaxLongSide(), imgIn.getWidth()) / (double) imgIn.getWidth();
        } else {
            // vertical image
            scale = Math.min(applicationProperties.getImage().getMaxLongSide(), imgIn.getHeight()) / (double) imgIn.getHeight();
        }

        BufferedImage thumbnailOut = new BufferedImage((int) (scale * imgIn.getWidth()),
            (int) (scale * imgIn.getHeight()),
            imgIn.getType());

        Graphics2D g = thumbnailOut.createGraphics();

        AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
        g.drawImage(imgIn, transform, DUMMY_OBSERVER);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnailOut, "jpeg", baos);

        return baos.toByteArray();
    }

}
