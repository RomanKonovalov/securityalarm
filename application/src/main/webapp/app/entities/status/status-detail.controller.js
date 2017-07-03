(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('StatusDetailController', StatusDetailController);

    StatusDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Status', 'uiGmapGoogleMapApi'];

    function StatusDetailController($scope, $rootScope, $stateParams, previousState, entity, Status, uiGmapGoogleMapApi) {
        var vm = this;

        vm.status = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('securityalarmApp:statusUpdate', function(event, result) {
            vm.status = result;
        });
        $scope.$on('$destroy', unsubscribe);

        $scope.map = {center: { latitude: entity.latitude, longitude: entity.longitude }, zoom: 16};

        $scope.marker = {
            id: 1,
            coords: {latitude: entity.latitude, longitude: entity.longitude},
            options: { draggable: false , title: 'Current position'}
        };

    }
})();
