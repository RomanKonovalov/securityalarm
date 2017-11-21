package com.romif.securityalarm.service;

import com.romif.securityalarm.domain.Image;
import com.romif.securityalarm.repository.ImageRepository;
import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import io.humble.video.customio.HumbleIO;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

@Service
public class VideoService {

    private final Logger log = LoggerFactory.getLogger(VideoService.class);

    @Inject
    private ImageRepository imageRepository;

    public void getVideoH264(List<Long> ids, OutputStream outputStream) {

        String url = HumbleIO.map(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                try {
                    outputStream.write(b);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            public void flush() throws IOException {
                outputStream.flush();
            }

            @Override
            public void close() throws IOException {
                outputStream.close();
            }
        });

        writeVideo(ids, url, "h264");

    }

    public void getVideoMp4(List<Long> ids, OutputStream outputStream) {
        try {
            File temp = File.createTempFile("video.mp4", ".tmp");
            String url = HumbleIO.map(outputStream);

            writeVideo(ids, url, "mp4");

            IOUtils.copyLarge(new FileInputStream(temp), outputStream);
        } catch (IOException e) {
            log.error("Can't create video", e);
        }
    }

    private void writeVideo(List<Long> ids, String url, String formatString) {
        Muxer muxer = null;
        try {
            final Rational framerate = Rational.make(1, 5);

            muxer = Muxer.make(url, null, formatString);

            final MuxerFormat format = muxer.getFormat();
            final Codec codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());

            Encoder encoder = Encoder.make(codec);

            encoder.setWidth(640);
            encoder.setHeight(480);
            // We are going to use 420P as the format because that's what most video formats these days use
            final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
            encoder.setPixelFormat(pixelformat);
            encoder.setTimeBase(framerate);

            if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
                encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

            encoder.open(null, null);


            /** Add this stream to the muxer. */
            muxer.addNewStream(encoder);

            /** And open the muxer for business. */
            muxer.open(null, null);

            MediaPictureConverter converter = null;
            final MediaPicture picture = MediaPicture.make(
                encoder.getWidth(),
                encoder.getHeight(),
                pixelformat);
            picture.setTimeBase(framerate);

            final MediaPacket packet = MediaPacket.make();
            int i = 0;

            for (List<Long> partition : ListUtils.partition(ids, 100)) {
                for (Image image : imageRepository.findByIds(partition)) {
                    i++;
                    BufferedImage imgIn = ImageIO.read(new ByteArrayInputStream(image.getRawImage()));
                    final BufferedImage screen = convertToType(imgIn, BufferedImage.TYPE_3BYTE_BGR);
                    if (converter == null)
                        converter = MediaPictureConverterFactory.createConverter(screen, picture);
                    converter.toPicture(picture, screen, i);

                    do {
                        encoder.encode(packet, picture);
                        if (packet.isComplete())
                            muxer.write(packet, false);
                    } while (packet.isComplete());

                }
            }


            do {
                encoder.encode(packet, null);
                if (packet.isComplete())
                    muxer.write(packet, false);
            } while (packet.isComplete());

            muxer.close();

        } catch (Exception e) {
            log.error("Can't create video", e);
            throw new RuntimeException(e);
        }
    }

    public void getVideo(List<Image> images, OutputStream outputStream) throws AWTException, IOException, InterruptedException {

        /**
         * Set up the AWT infrastructure to take screenshots of the desktop.
         */


        String url = HumbleIO.map(outputStream);

        final Rational framerate = Rational.make(1, 4);

        /** First we create a muxer using the passed in filename and formatname if given. */

        final Muxer muxer = Muxer.make(url, null, "flv");

        /** Now, we need to decide what type of codec to use to encode video. Muxers
         * have limited sets of codecs they can use. We're going to pick the first one that
         * works, or if the user supplied a codec name, we're going to force-fit that
         * in instead.
         */
        final MuxerFormat format = muxer.getFormat();
        final Codec codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());

        /**
         * Now that we know what codec, we need to create an encoder
         */
        Encoder encoder = Encoder.make(codec);

        encoder.setWidth(640);
        encoder.setHeight(480);
        // We are going to use 420P as the format because that's what most video formats these days use
        final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelformat);
        encoder.setTimeBase(framerate);

        /** An annoynace of some formats is that they need global (rather than per-stream) headers,
         * and in that case you have to tell the encoder. And since Encoders are decoupled from
         * Muxers, there is no easy way to know this beyond
         */
        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

        /** Open the encoder. */
        /*KeyValueBag keyValueBag = KeyValueBag.make();
        keyValueBag.setValue("coder", "0");
        keyValueBag.setValue("bf", "0");
        keyValueBag.setValue("flags", "-loop");
        keyValueBag.setValue("wpredp", "0");
        KeyValueBag keyValueBag1 = KeyValueBag.make();
        encoder.open(keyValueBag, keyValueBag1);*/
        encoder.open(null, null);


        /** Add this stream to the muxer. */
        muxer.addNewStream(encoder);

        /** And open the muxer for business. */
        muxer.open(null, null);

        /** Next, we need to make sure we have the right MediaPicture format objects
         * to encode data with. Java (and most on-screen graphics programs) use some
         * variant of Red-Green-Blue image encoding (a.k.a. RGB or BGR). Most video
         * codecs use some variant of YCrCb formatting. So we're going to have to
         * convert. To do that, we'll introduce a MediaPictureConverter object later. object.
         */
        MediaPictureConverter converter = null;
        final MediaPicture picture = MediaPicture.make(
            encoder.getWidth(),
            encoder.getHeight(),
            pixelformat);
        picture.setTimeBase(framerate);

        /** Now begin our main loop of taking screen snaps.
         * We're going to encode and then write out any resulting packets. */
        final MediaPacket packet = MediaPacket.make();
        int i = 0;
        for (Image image : images) {
            i++;
            /** Make the screen capture && convert image to TYPE_3BYTE_BGR */
            BufferedImage imgIn = ImageIO.read(new ByteArrayInputStream(image.getRawImage()));
            final BufferedImage screen = convertToType(imgIn, BufferedImage.TYPE_3BYTE_BGR);


            /** This is LIKELY not in YUV420P format, so we're going to convert it using some handy utilities. */
            if (converter == null)
                converter = MediaPictureConverterFactory.createConverter(screen, picture);
            converter.toPicture(picture, screen, i);

            do {
                encoder.encode(packet, picture);
                if (packet.isComplete())
                    muxer.write(packet, false);
            } while (packet.isComplete());

        }


        /** Encoders, like decoders, sometimes cache pictures so it can do the right key-frame optimizations.
         * So, they need to be flushed as well. As with the decoders, the convention is to pass in a null
         * input until the output is not complete.
         */
        do {
            encoder.encode(packet, null);
            if (packet.isComplete())
                muxer.write(packet, false);
        } while (packet.isComplete());

        /** Finally, let's clean up after ourselves. */
        muxer.close();

    }


    /**
     * Convert a {@link BufferedImage} of any type, to {@link BufferedImage} of a
     * specified type. If the source image is the same type as the target type,
     * then original image is returned, otherwise new image of the correct type is
     * created and the content of the source image is copied into the new image.
     *
     * @param sourceImage the image to be converted
     * @param targetType  the desired BufferedImage type
     * @return a BufferedImage of the specifed target type.
     * @see BufferedImage
     */

    private static BufferedImage convertToType(BufferedImage sourceImage,
                                               int targetType) {
        BufferedImage image;

        // if the source image is already the target type, return the source image

        if (sourceImage.getType() == targetType)
            image = sourceImage;

            // otherwise create a new image of the target type and draw the new
            // image

        else {
            image = new BufferedImage(sourceImage.getWidth(),
                sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;
    }

}
