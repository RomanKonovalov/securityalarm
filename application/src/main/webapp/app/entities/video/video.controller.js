(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('VideoController', StatusController);

    StatusController.$inject = ['$scope', '$state', 'Status', 'AlertService', 'devices', '$sce', 'AuthServerProvider', 'DateUtils'];

    function StatusController ($scope, $state, Status, AlertService, devices, $sce, AuthServerProvider, DateUtils) {
        var vm = this;
        var startDate = new Date(new Date().getTime() - 30 * 1000 * 60),
            endDate = new Date();

        vm.devices = devices;
        vm.device = {};


        vm.refresh = function loadAll (device) {
            //startDate = new Date(new Date().getTime() - 60 * 1000 * 60);
            //endDate = new Date();
            /*$scope.myDatetimeRange.date.from = startDate;
            $scope.myDatetimeRange.date.to = endDate;
            $scope.myDatetimeRange.time.from = (startDate.getTime() - midnight.getTime()) / 1000 / 60;
            $scope.myDatetimeRange.time.to = (endDate.getTime() - midnight.getTime()) / 1000 / 60;*/

            Status.images({
                startDate: startDate,
                endDate: endDate,
                device: device.id
            }, function (data) {
                vm.images = data;
                vm.videoSources = _.map(data).map(function(images) {
                    return getVideoSource(images);
                });
            }, onError);

            function onError(error) {
                AlertService.error(error.data.message);
            }
        };

        var getVideoSource = function (images) {
            var token = AuthServerProvider.getToken().access_token;
            var result =  '/api/statuses/video.h264?device=' + vm.device.id + '&endDate=' + images.endDate + '&startDate=' + images.startDate + '&videoNo=0' + '&access_token=' + token;
            return result.replace(/\+/g, '%2B');
        };

        vm.playVideo = function downloadVideo (device) {
            var token = AuthServerProvider.getToken().access_token;
            $scope.config.sources[0].src = $scope.config.sources[0].src ? null : '/api/statuses/video?device=' + device.id + '&endDate=' + endDate.toISOString() + '&startDate=' + startDate.toISOString() + '&videoNo=0' + '&access_token=' + token;

        };
        var midnight = new Date();
        midnight.setHours(0, 0, 0);
        $scope.myDatetimeRange = {
            date: {
                from: new Date(),
                to: new Date(),
                max: new Date(),
                options: {
                    from: {
                        showWeeks: false
                    },
                    to: {
                        showWeeks: false
                    }
                }
            },
            time: {
                from: (startDate.getTime() - midnight.getTime()) / 1000 / 60, // default low value
                to: (endDate.getTime() - midnight.getTime()) / 1000 / 60, // default high value
                step: 1, // step width
                minRange: 30, // min range
                hours24: true // true for 24hrs based time | false for PM and AM
            }
        };
        $scope.myDatetimeLabels = {
            date: {
                from: 'Start date',
                to: 'End date'
            }
        };

        $scope.config = {
            sources: [
                {
                    type: "video/flv"
                }
            ],
            tracks: [],
            theme: "bower_components/videogular-themes-default/videogular.css"
        };

        $scope.whenTimeChange = function (data) {
            if (data.date.from.toISOString().slice(0, 10) === data.date.to.toISOString().slice(0, 10)) {
                $scope.myDatetimeRange.hasTimeSliders = true;
            } else {
                $scope.myDatetimeRange.hasTimeSliders = false;
            }

            startDate = data.date.from;
            startDate.setHours(0, 0, 0);
            startDate = new Date(startDate.getTime() + data.time.from * 1000 * 60);
            endDate = data.date.to;
            endDate.setHours(0, 0, 0);
            endDate = new Date(endDate.getTime() + data.time.to * 1000 * 60);
            if (vm.device.id) {
                vm.refresh(vm.device);
            }

        };

        devices.$promise.then(function (result) {
            if (result.length > 0) {
                vm.device = result[0];
                vm.refresh(vm.device);
            }
        });


    }
})();
