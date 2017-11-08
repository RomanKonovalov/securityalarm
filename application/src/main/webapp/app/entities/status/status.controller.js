(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('StatusController', StatusController);

    StatusController.$inject = ['$scope', '$state', 'Status', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams', 'devices', '$sce', 'AuthServerProvider', 'DateUtils'];

    function StatusController ($scope, $state, Status, ParseLinks, AlertService, paginationConstants, pagingParams, devices, $sce, AuthServerProvider, DateUtils) {
        var vm = this;
        var startDate = new Date(new Date().getTime() - 60 * 1000 * 60),
            endDate = new Date();

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;

        vm.devices = devices;
        vm.device = {};


        vm.refresh = function loadAll (device) {
            //startDate = new Date(new Date().getTime() - 60 * 1000 * 60);
            //endDate = new Date();
            /*$scope.myDatetimeRange.date.from = startDate;
            $scope.myDatetimeRange.date.to = endDate;
            $scope.myDatetimeRange.time.from = (startDate.getTime() - midnight.getTime()) / 1000 / 60;
            $scope.myDatetimeRange.time.to = (endDate.getTime() - midnight.getTime()) / 1000 / 60;*/

            Status.query({
                startDate: startDate,
                endDate: endDate,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort(),
                device: device.id
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
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        };

        vm.playVideo = function downloadVideo (device) {
            var token = AuthServerProvider.getToken().access_token;
            $scope.config.sources[0].src = $scope.config.sources[0].src ? null : '/api/statuses/video?device=' + device.id + '&endDate=' + endDate.toISOString() + '&startDate=' + startDate.toISOString() + '&access_token=' + token;

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
