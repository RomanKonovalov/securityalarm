(function () {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'DeviceTracker'];

    function HomeController($scope, Principal, LoginService, $state, DeviceTracker) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        vm.deviceActivity = {};
        vm.deviceActivity.location = {};
        $scope.$on('authenticationSuccess', function () {
            getAccount();
        });

        getAccount();

        vm.isoCode = 'BYN';

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;

                DeviceTracker.subscribe();

                DeviceTracker.receive().then(null, null, function (deviceActivity) {
                    if (deviceActivity.id === 5) {
                        vm.deviceActivity = deviceActivity;
                        vm.map = {
                            center: {
                                latitude: vm.deviceActivity.location.latitude,
                                longitude: vm.deviceActivity.location.longitude
                            }, zoom: 16
                        };

                        vm.marker = {
                            id: 1,
                            coords: {
                                latitude: vm.deviceActivity.location.latitude,
                                longitude: vm.deviceActivity.location.longitude
                            },
                            options: {draggable: false, title: 'Current position'}
                        };

                        vm.circles = [
                            {
                                id: 1,
                                center: {
                                    latitude: vm.deviceActivity.location.latitude,
                                    longitude: vm.deviceActivity.location.longitude
                                },
                                radius: vm.deviceActivity.location.latitudeError,
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
                        ];
                    }
                });
            });
        }

        function register() {
            $state.go('register');
        }
    }
})();
