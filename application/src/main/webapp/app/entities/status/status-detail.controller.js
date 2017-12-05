(function () {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('StatusDetailController', StatusDetailController);

    StatusDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Status', 'uiGmapGoogleMapApi'];

    function StatusDetailController($scope, $rootScope, $stateParams, previousState, entity, Status, uiGmapGoogleMapApi) {
        var vm = this;

        vm.status = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('securityalarmApp:statusUpdate', function (event, result) {
            vm.status = result;
        });
        $scope.$on('$destroy', unsubscribe);

        $scope.map = entity.location ? {
            center: {
                latitude: entity.location.latitude,
                longitude: entity.location.longitude
            }, zoom: 16
        } : null;

        $scope.marker = entity.location ? {
            id: 1,
            coords: {latitude: entity.location.latitude, longitude: entity.location.longitude},
            options: {draggable: false, title: 'Current position'}
        } : null;

        vm.circles = entity.location ? [
            {
                id: 1,
                center: {
                    latitude: entity.location.latitude,
                    longitude: entity.location.longitude
                },
                radius: entity.location.latitudeError,
                stroke: {
                    color: '#868686',
                    weight: 1,
                    opacity: 1
                },
                fill: {
                    color: '#8cd3ec',
                    opacity: 0.5
                },
                geodesic: true, // optional: defaults to false
                draggable: false, // optional: defaults to false
                clickable: false, // optional: defaults to true
                editable: false, // optional: defaults to false
                visible: true, // optional: defaults to true
                control: {}
            }
        ] : null;

    }
})();
