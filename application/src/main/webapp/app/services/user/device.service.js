(function () {
    'use strict';

    angular
        .module('securityalarmApp')
        .factory('Device', Device);

    Device.$inject = ['$resource'];

    function Device ($resource) {
        var service = $resource('api/devices/:login/:action', {}, {
            'query': {method: 'GET', isArray: true},
            'save': { method:'POST' },
            'update': { method:'PUT' },
            'delete':{ method:'DELETE'},
            'login': { method:'POST', params: {'login': '@login', action: 'login'} },
            'logout': { method:'POST', params: {'login': '@login', action: 'logout'} },
            'config': { method:'POST', params: {'login': '@login', action: 'config'} },
            'reboot': { method:'POST', params: {'login': '@login', action: 'reboot'} },
            'halt': { method:'POST', params: {'login': '@login', action: 'halt'} }
        });

        return service;
    }
})();
