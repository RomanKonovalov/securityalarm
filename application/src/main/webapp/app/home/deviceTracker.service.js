(function() {
    'use strict';
    /* globals SockJS, Stomp */

    angular
        .module('securityalarmApp')
        .factory('DeviceTracker', DeviceTracker);

    DeviceTracker.$inject = ['$rootScope', '$window', '$cookies', '$http', '$q', '$localStorage'];

    function DeviceTracker ($rootScope, $window, $cookies, $http, $q, $localStorage) {
        var stompClient = null;
        var subscriber = null;
        var listener = $q.defer();
        var connected = $q.defer();
        var alreadyConnectedOnce = false;

        var service = {
            connect: connect,
            disconnect: disconnect,
            receive: receive,
            sendDeviceTrackingControl: sendDeviceTrackingControl,
            subscribe: subscribe,
            unsubscribe: unsubscribe
        };

        return service;

        function connect () {
            connected = $q.defer();
            //building absolute path so that websocket doesn't fail when deploying with a context path
            var loc = $window.location;
            var url = '//' + loc.host + loc.pathname + 'websocket/deviceTracker';
            /*jshint camelcase: false */
            var authToken = angular.fromJson($localStorage.authenticationToken).access_token;
            url += '?access_token=' + authToken;
            var socket = new SockJS(url);
            stompClient = Stomp.over(socket);
            stompClient.debug = null;
            var stateChangeStart;
            var headers = {};
            stompClient.connect(headers, function() {
                connected.resolve('success');
            });
            $rootScope.$on('$destroy', function () {
                if(angular.isDefined(stateChangeStart) && stateChangeStart !== null){
                    stateChangeStart();
                }
            });
        }

        function disconnect () {
            if (stompClient !== null) {
                stompClient.disconnect();
                stompClient = null;
            }
        }

        function receive () {
            return listener.promise;
        }

        function sendDeviceTrackingControl(start) {
            if (stompClient !== null && stompClient.connected) {
                stompClient
                    .send('/topic/deviceTrackingControl',
                    {},
                    angular.toJson({'start': start}));
            }
        }

        function subscribe () {
            connected.promise.then(function() {
                subscriber = stompClient.subscribe('/user/queue/deviceTracker', function(data) {
                    listener.notify(angular.fromJson(data.body));
                });
            }, null, null);
        }

        function unsubscribe () {
            if (subscriber !== null) {
                subscriber.unsubscribe();
            }
            listener = $q.defer();
        }
    }
})();
