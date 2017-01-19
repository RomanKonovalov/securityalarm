(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('MapController', MapController);

    MapController.$inject = ['$scope', '$state', 'uiGmapGoogleMapApi', 'Status', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function MapController ($scope, $state, uiGmapGoogleMapApi, Status, ParseLinks, AlertService, paginationConstants, pagingParams) {

        $scope.map = {center: { latitude: 53.878338, longitude: 30.365049 }, zoom: 12, bounds: {}, control: {}};
        $scope.polylines = [];

        $scope.marker = {};
        $scope.marker.home = {
            id: 0,
            coords: {
                latitude: 53.878338,
                longitude: 30.365049
            },
            options: { draggable: false , title: 'Home'}
        };

        var vm = this;


        loadAll();

        function loadAll () {
            Status.query({
                page: 0,
                size: 100,
                sort: 'createdDate,desc'
            }, onSuccess, onError);

            function onSuccess(data) {

                uiGmapGoogleMapApi.then(function() {

                        var lastPosition = data.slice(-1).pop();
                        $scope.map.center = {latitude: lastPosition.latitude, longitude: lastPosition.longitude};

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

                        $scope.map.control.getGMap().fitBounds(bounds);

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
