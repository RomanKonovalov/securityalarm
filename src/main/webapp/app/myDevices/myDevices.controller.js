(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('MyDevicesController', MyDevicesController);

    MyDevicesController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'Alarm', 'Devices', 'Alarms'];

    function MyDevicesController ($scope, Principal, LoginService, $state, Alarm, Devices, Alarms) {

        $scope.devices = Devices;

        $scope.alarms = Alarms;

        $scope.isActivated = function (name) {
            return _.some($scope.alarms, { 'deviceName': name });
        };

        $scope.activateAlarm = function (name) {
            Alarm.save({'deviceName': name}, function () {
                $scope.alarms = Alarm.query();
            });
        };

        $scope.deactivateAlarm = function (name) {
            var alarm = _.find($scope.alarms, { 'deviceName': name });
            Alarm.delete({'id': alarm.id}, function () {
                $scope.alarms = Alarm.query();
            });
        };

        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        function register () {
            $state.go('register');
        }
    }
})();
