(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('StatusController', StatusController);

    StatusController.$inject = ['$scope', '$state', 'Status', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams', 'devices'];

    function StatusController ($scope, $state, Status, ParseLinks, AlertService, paginationConstants, pagingParams, devices) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;

        vm.devices = devices;
        vm.device = {};


        vm.refresh = function loadAll (device) {
            Status.query({
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
        }

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
