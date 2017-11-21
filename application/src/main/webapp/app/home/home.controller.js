(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'DeviceTracker'];

    function HomeController ($scope, Principal, LoginService, $state, DeviceTracker) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        vm.deviceActivity;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;

                DeviceTracker.subscribe();

                DeviceTracker.receive().then(null, null, function(deviceActivity) {
                    vm.deviceActivity = deviceActivity;
                });
            });
        }
        function register () {
            $state.go('register');
        }
    }
})();
