(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('MapController', MapController);

    MapController.$inject = ['$scope', '$interval', 'uiGmapGoogleMapApi', 'Status', 'Account', 'AlertService', 'Principal','DateUtils', 'devices'];

    function MapController ($scope, $interval, uiGmapGoogleMapApi, Status, Account,  AlertService, Principal, DateUtils, devices) {

        $scope.devices = devices;
        $scope.device = {};

        var limit = 100;

        var midnight = new Date();
        midnight.setHours(0, 0, 0);

        var startDate = new Date(new Date().getTime() - 60 * 1000 * 60);
        var endDate = new Date();

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
            if ($scope.device.id) {
                loadAll();
            }
        };

        $scope.devices.$promise.then(function (result) {
            if (result.length > 0) {
                $scope.device = result[0];
                loadAll();
            }
        });



        $scope.map = {bounds: {}, control: {}};

        $scope.polylines = [];

        var homePosition = Account.location;

        $scope.marker = {};

        $scope.marker.home = {
            id: 0,
            coords: homePosition,
            options: { draggable: false , title: 'Home'}
        };

        var stop = $interval(loadAll, 60000);

        $scope.$on('$destroy', function() {
            $interval.cancel(stop);
        });

        function loadAll () {
            Status.locations({
                startDate: startDate,
                endDate: endDate,
                device: $scope.device.id
            }, onSuccess, onError);

            function onSuccess(data) {

                if (data.length ===0) {
                    AlertService.warning('You don\'t have any records for this date range');
                }

                uiGmapGoogleMapApi.then(function() {

                    var newPosition = _.find(data, function(o) {
                        return !isNaN(o.latitude) && !isNaN(o.longitude);
                    });

                    var lastPosition = newPosition ? newPosition : homePosition;

                    $scope.map.center = {latitude: lastPosition.latitude, longitude: lastPosition.longitude};

                    $scope.marker.current = !data[0] ? undefined : {
                        id: 1,
                        coords: {latitude: lastPosition.latitude, longitude: lastPosition.longitude},
                        options: {
                            draggable: false ,
                            title: 'Current position',
                            icon:'//developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png'}
                    };

                    var bounds = new google.maps.LatLngBounds();
                    for (var i in data) {
                        if (data[i] && !isNaN(data[i].latitude) && !isNaN(data[i].longitude)) {
                            var position = new google.maps.LatLng(data[i].latitude, data[i].longitude);
                            bounds.extend(position);
                        }
                    }

                    $scope.map.bounds = !newPosition ? undefined : {
                        northeast: {
                            latitude: bounds.getNorthEast().lat(),
                            longitude: bounds.getNorthEast().lng()
                        },
                        southwest: {
                            latitude: bounds.getSouthWest().lat(),
                            longitude: bounds.getSouthWest().lng()
                        }
                    };

                    $scope.map.zoom = !newPosition ? 16 : undefined;

                    $scope.polylines = [
                        {
                            id: 1,
                            path: data,
                            stroke: {
                                color: '#6060FB',
                                weight: 2
                            },
                            editable: false,
                            draggable: false,
                            geodesic: true,
                            visible: true,
                            icons: [{
                                icon: {
                                    path: google.maps.SymbolPath.BACKWARD_OPEN_ARROW
                                },
                                offset: '10px',
                                repeat: '50px'
                            }]
                        }]
                    }
                );
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        $scope.loadAll = loadAll;

    }
})();
