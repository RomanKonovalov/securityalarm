package com.romif.securityalarm.service;

import com.romif.securityalarm.config.ApplicationProperties;
import com.romif.securityalarm.domain.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service
@Transactional
public class ImageService {

    private static final ImageObserver DUMMY_OBSERVER = (img, infoflags, x, y, width, height) -> true;

    @Inject
    private ApplicationProperties applicationProperties;

    @Inject
    private StatusService statusService;

    public void saveImage(MultipartFile file, Status status) throws IOException {

        File folder = new File(applicationProperties.getImage().getStoragePath() + File.separator + status.getCreatedBy());
        folder.mkdirs();
        File imageFile = new File(folder, File.separator + status.getId() + ".jpg");
        file.transferTo(imageFile);

        BufferedImage imgIn = ImageIO.read(imageFile);

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

        status.setThumbnail(baos.toByteArray());

        statusService.save(status);
    }

}
