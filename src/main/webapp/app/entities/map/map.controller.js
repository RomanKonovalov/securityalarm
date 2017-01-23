(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('MapController', MapController);

    MapController.$inject = ['$scope', '$interval', 'uiGmapGoogleMapApi', 'Status', 'Account', 'AlertService', 'Principal','DateUtils'];

    function MapController ($scope, $interval, uiGmapGoogleMapApi, Status, Account,  AlertService, Principal, DateUtils) {

        var limit = 100;
        loadAll();

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
            limit = 1000;
            loadAll();
        };

        $scope.yesterday = function() {
            $scope.startDate = minDate(new Date());
            $scope.startDate.setDate($scope.startDate.getDate() - 1);
            $scope.endDate = maxDate(new Date());
            $scope.endDate.setDate($scope.endDate.getDate() - 1);
            limit = 1000;
            loadAll();
        };

        $scope.dateRangeChange = function () {
            $scope.startDate = minDate($scope.startDate);
            $scope.endDate = maxDate($scope.endDate);
            limit = 1000;
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

        var homePosition = { latitude: Account.location.latitude, longitude: Account.location.longitude };

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
                endDate: DateUtils.convertLocalDateToServer($scope.endDate)
            }, onSuccess, onError);

            function onSuccess(data) {

                if (data.length ===0) {
                    AlertService.warning('You don\'t have any records for this date range');
                }

                $scope.startDate = $scope.startDate || new Date(data[data.length - 1].createdDate);

                $scope.endDate = $scope.endDate || new Date(data[0].createdDate);

                uiGmapGoogleMapApi.then(function() {

                    var lastPosition = data[0] || homePosition;

                    $scope.map.center = { latitude: lastPosition.latitude, longitude: lastPosition.longitude };

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
                        if (data[i].latitude && data[i].longitude) {
                            var position = new google.maps.LatLng(data[i].latitude, data[i].longitude);
                            bounds.extend(position);
                        }
                    }

                    $scope.map.bounds = !data[0] ? undefined : {
                        northeast: {
                            latitude: bounds.getNorthEast().lat(),
                            longitude: bounds.getNorthEast().lng()
                        },
                        southwest: {
                            latitude: bounds.getSouthWest().lat(),
                            longitude: bounds.getSouthWest().lng()
                        }
                    };

                    $scope.map.zoom = !data[0] ? 16 : undefined;

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

    }
})();
