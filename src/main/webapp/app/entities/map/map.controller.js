(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('MapController', MapController);

    MapController.$inject = ['$scope', '$state', 'uiGmapGoogleMapApi', 'Status', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function MapController ($scope, $state, uiGmapGoogleMapApi, Status, ParseLinks, AlertService, paginationConstants, pagingParams) {

        $scope.map = {center: { latitude: 53.878338, longitude: 30.365049 }, zoom: 12, bounds: {}, control: {}};
        $scope.polylines = [];

        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;

        loadAll();

        function loadAll () {
            Status.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.statuses = data;
                vm.page = pagingParams.page;

                uiGmapGoogleMapApi.then(function() {

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

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }
    }
})();
