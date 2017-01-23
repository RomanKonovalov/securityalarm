(function() {
    'use strict';

    angular
        .module('securityalarmApp')
        .controller('SettingsController', SettingsController);

    SettingsController.$inject = ['$scope','Principal', 'Auth', 'Account'];

    function SettingsController ($scope, Principal, Auth, Account) {

        $scope.locationpickerOptions = {
            location: {
                latitude: Account.location.latitude,
                longitude: Account.location.longitude
            },
            inputBinding: {
                latitudeInput: $('#us1-lat'),
                longitudeInput: $('#us1-lon'),
                locationNameInput: $('#us1-address')
            },
            radius: 0,
            enableAutocomplete: true,
            autocompleteOptions: {
                componentRestrictions: {country: 'by'}
            }
        };

        var vm = this;

        vm.error = null;
        vm.save = save;
        vm.settingsAccount = null;
        vm.success = null;

        /**
         * Store the "settings account" in a separate variable, and not in the shared "account" variable.
         */
        var copyAccount = function (account) {
            return {
                activated: account.activated,
                email: account.email,
                firstName: account.firstName,
                langKey: account.langKey,
                lastName: account.lastName,
                login: account.login,
                location: {latitude: account.location.latitude, longitude: account.location.longitude},
                devices: account.devices
            };
        };

        Principal.identity().then(function(account) {
            vm.settingsAccount = copyAccount(account);
        });

        function save () {
            Auth.updateAccount(vm.settingsAccount).then(function() {
                vm.error = null;
                vm.success = 'OK';
                Principal.identity(true).then(function(account) {
                    vm.settingsAccount = copyAccount(account);
                });
            }).catch(function() {
                vm.success = null;
                vm.error = 'ERROR';
            });
        }
    }
})();
