/*
 * Source : http://stuntsnippets.com/javascript-countdown/
 */

var jsCountdown = function () {
	var time_left = 10; //number of seconds for countdown
	var output_element_id = 'countdown_time';
	var keep_counting = 1;
	var no_time_left_message = 'Countdown over';

	function countdown() {
		if(time_left < 2) {
			keep_counting = 0;
		}

		time_left = time_left - 1;
	}

	function add_leading_zero(n) {
		if(n.toString().length < 2) {
			return '0' + n;
		} else {
			return n;
		}
	}

	function format_output() {
		var hours, minutes, seconds;
		seconds = time_left % 60;
		minutes = Math.floor(time_left / 60) % 60;
		hours = Math.floor(time_left / 3600);

		seconds = add_leading_zero( seconds );
		minutes = add_leading_zero( minutes );
		hours = add_leading_zero( hours );

		return hours + ':' + minutes + ':' + seconds;
	}

	function show_time_left() {
		document.getElementById(output_element_id).innerHTML = format_output();//time_left;
	}

	function no_time_left() {
		//document.getElementById(output_element_id).innerHTML = no_time_left_message;
		window.location.reload();
	}

	return {
		count: function () {
			countdown();
			show_time_left();
		},
		timer: function () {
			jsCountdown.count();

			if(keep_counting) {
				setTimeout("jsCountdown.timer();", 1000);
			} else {
				no_time_left();
			}
		},
		init: function (time, element_id) {
			time_left = time;
			output_element_id = element_id;
			jsCountdown.timer();
		}
	};
}();

//time to countdown in seconds, and element ID
//jsCountdown.init(3673, 'countdown_time');