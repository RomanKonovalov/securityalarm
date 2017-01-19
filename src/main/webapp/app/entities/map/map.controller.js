(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('MapController', MapController);

    MapController.$inject = ['$scope', '$interval', 'uiGmapGoogleMapApi', 'Status', 'AlertService', 'Principal'];

    function MapController ($scope, $interval, uiGmapGoogleMapApi, Status,  AlertService, Principal) {
        $scope.map = {bounds: {}, control: {}};

        $scope.polylines = [];

        $scope.marker = {};

        Principal.identity(false).then(function (account) {
            $scope.marker.home = {
                id: 0,
                coords: {
                    latitude: account.location.latitude,
                    longitude: account.location.longitude
                },
                options: { draggable: false , title: 'Home'}
            };
        });

        loadAll();

        var stop = $interval(loadAll, 60000);

        $scope.$on('$destroy', function() {
            $interval.cancel(stop);
        });

        function loadAll () {
            Status.query({
                page: 0,
                size: 100,
                sort: 'createdDate,desc'
            }, onSuccess, onError);

            function onSuccess(data) {

                uiGmapGoogleMapApi.then(function() {

                    var lastPosition = data[0];

                    $scope.map.center = { latitude: lastPosition.latitude, longitude: lastPosition.longitude };

                    $scope.marker.current = {
                        id: 1,
                        coords: {latitude: lastPosition.latitude, longitude: lastPosition.longitude},
                        options: { draggable: false , title: 'Current position'}
                    };

                    var bounds = new google.maps.LatLngBounds();
                    for (var i in data) {
                        if (data[i].latitude && data[i].longitude) {
                            var position = new google.maps.LatLng(data[i].latitude, data[i].longitude);
                            bounds.extend(position);
                        }
                    }

                    //$scope.map.control.getGMap().fitBounds(bounds);

                    $scope.map.bounds = {
                        northeast: {
                            latitude: bounds.getNorthEast().lat(),
                            longitude: bounds.getNorthEast().lng()
                        },
                        southwest: {
                            latitude: bounds.getSouthWest().lat(),
                            longitude: bounds.getSouthWest().lng()
                        }
                    };

                    //$scope.map.bounds = bounds;

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
