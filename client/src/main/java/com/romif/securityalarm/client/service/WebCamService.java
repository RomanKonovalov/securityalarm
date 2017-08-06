package com.romif.securityalarm.client.service;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;


@Service
public class WebCamService {

    public byte[] getImage() throws IOException {

        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);

        grabber.start();
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        IplImage grabbedImage = converter.convert(grabber.grab());

        cvSaveImage("temp.jpeg", grabbedImage);
        byte[] bytes = Files.readAllBytes(Paths.get("temp.jpeg"));

        grabber.stop();

        return bytes;
    }


}
