package com.romif.securityalarm.client.web.rest;

import com.romif.securityalarm.client.service.MotionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/motion")
public class MotionResource {

    private final Logger log = LoggerFactory.getLogger(MotionResource.class);

    @Autowired
    private MotionService motionService;

    @RequestMapping("/pauseDetection")
    public ResponseEntity<Void> pauseDetection() {
        log.debug("REST request to pause Detection");
        if (motionService.pauseDetection()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping("/startDetection")
    public ResponseEntity<Void> startDetection() {
        log.debug("REST request to start Detection");
        if (motionService.startDetection()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping("/statusDetection")
    public ResponseEntity<Boolean> statusDetection() {
        log.debug("REST request for status Detection");
        return ResponseEntity.ok(motionService.statusDetection());
    }

    @RequestMapping(method = RequestMethod.POST, path = "/snapshotInterval")
    public ResponseEntity<Void> setSnapshotInterval(@RequestParam("interval") int interval) {
        log.debug("REST request to set SnapshotInterval");
        if (motionService.setSnapshotInterval(interval)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/threshold")
    public ResponseEntity<Void> setThreshold(@RequestParam("threshold") int threshold) {
        log.debug("REST request to set Threshold");
        if (motionService.setThreshold(threshold)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/config")
    public ResponseEntity<Void> setConfig(@RequestParam("property") String property, @RequestParam("value") String value) {
        log.debug("REST request to set Config");
        if (motionService.setConfig(property, value)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/config")
    public ResponseEntity<String> getConfig(@RequestParam("property") String property) {
        log.debug("REST request to set Config");
        return ResponseEntity.ok(motionService.getConfig(property));
    }

}
