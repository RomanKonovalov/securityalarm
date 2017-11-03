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

        $scope.devices.$promise.then(function (result) {
            if (result.length > 0) {
                $scope.device = result[0];
                $scope.last6hours();
            }
        });

        var minDate = function (date) {
            date.setHours(0,0,0,0);
            return date;
        };
        var maxDate = function (date) {
            date.setHours(23,59,59,99);
            return date;
        };

        $scope.today = function() {
            $scope.startDate = minDate(new Date());
            $scope.endDate = maxDate(new Date());
            limit = 1440;
            loadAll();
        };

        $scope.yesterday = function() {
            $scope.startDate = minDate(new Date());
            $scope.startDate.setDate($scope.startDate.getDate() - 1);
            $scope.endDate = maxDate(new Date());
            $scope.endDate.setDate($scope.endDate.getDate() - 1);
            limit = 1440;
            loadAll();
        };

        $scope.last6hours = function() {
            var last6hours = new Date();
            last6hours.setHours(last6hours.getHours() - 6);
            $scope.startDate = last6hours;
            $scope.endDate = new Date();
            limit = 100;
            loadAll();
        };

        $scope.dateRangeChange = function () {
            $scope.startDate = minDate($scope.startDate);
            $scope.endDate = maxDate($scope.endDate);
            limit = 1440;
            loadAll();
        };

        $scope.open1 = function() {
            $scope.popup1.opened = true;
        };

        $scope.open2 = function() {
            $scope.popup2.opened = true;
        };

        $scope.popup1 = {
            opened: false
        };

        $scope.popup2 = {
            opened: false
        };

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
            Status.query({
                page: 0,
                size: limit,
                sort: 'createdDate,desc',
                startDate: DateUtils.convertLocalDateToServer($scope.startDate),
                endDate: DateUtils.convertLocalDateToServer($scope.endDate),
                device: $scope.device.id
            }, onSuccess, onError);

            function onSuccess(data) {

                if (data.length ===0) {
                    AlertService.warning('You don\'t have any records for this date range');
                }

                uiGmapGoogleMapApi.then(function() {

                    var newPosition = _.find(data, function(o) {
                        return !isNaN(o.location.latitude) && !isNaN(o.location.longitude);
                    });

                    var lastPosition = newPosition ? newPosition.location : homePosition;

                    $scope.map.center = lastPosition;

                    $scope.marker.current = !data[0] ? undefined : {
                        id: 1,
                        coords: lastPosition,
                        options: {
                            draggable: false ,
                            title: 'Current position',
                            icon:'//developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png'}
                    };

                    var bounds = new google.maps.LatLngBounds();
                    for (var i in data) {
                        if (data[i].location && !isNaN(data[i].location.latitude) && !isNaN(data[i].location.longitude)) {
                            var position = new google.maps.LatLng(data[i].location.latitude, data[i].location.longitude);
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

                    var path = _.filter(data, new function (el) {
                        return el && el.location && !isNaN(el.location.latitude) && !isNaN(el.location.longitude);
                    });

                    path = _.map(path, 'location');

                    $scope.polylines = [
                        {
                            id: 1,
                            path: path,
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
