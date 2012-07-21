function isdefined(variable) {
    return (typeof(window[variable]) == "undefined")?  false: true;
}

function getHideShowContent(baseId) {
  return document.getElementById(baseId + 'Content');
}

function getHideShowToggle(baseId) {
  return document.getElementById(baseId + 'Toggle');
}

function hideContent(baseId) {
  getHideShowContent(baseId).style.display = "none";
  toggle = getHideShowToggle(baseId);
  toggle.src = '/images/icons/show.png';
  toggle.alt = toggle.showLabel;
  toggle.title = toggle.showLabel;
}
    
function showContent(baseId) {
  getHideShowContent(baseId).style.display = "block";
  toggle = getHideShowToggle(baseId);
  toggle.src = '/images/icons/hide.png';
  toggle.alt = toggle.hideLabel;
  toggle.title = toggle.hideLabel;
}

function toggleDisplay(baseId) {
  if (getHideShowContent(baseId).style.display == "none") {
    showContent(baseId);
  } else {
    hideContent(baseId);
  }
}

function toggleVisibility(elementId, visibility) {
	var element = document.getElementById(elementId);
	if (visibility) {
		element.style.visibility = visibility;
	} else if (element.style.visibility == 'visible') {
		element.style.visibility = 'hidden';
	} else if (element.style.visibility == 'hidden') {
		element.style.visibility = 'visible';
	}
}

function popUp(url, width, height) {
  if (!width) {
  	width=600;
  }
  if (!height) {
  	height=600;
  }
  day = new Date();
  id = day.getTime();
  return window.open(url, id , "toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width="+width+",height="+height+",left = 420,top = 150");
}

function closePopup() {
  window.close();
}

function isEnterKeyEvent(event) {
  return (event.keyCode? event.keyCode : event.which? event.which : event.charCode) == 13;
}

function onEnterKeyPress(field, event) {
  if (isEnterKeyEvent(event)) {
	var form = field.form;
    for (var i = 0; i < form.elements.length; i++) {
    	var e = form.elements[i];
    	if (e.tagName.match(/button/i) && 
    	    e.className.match(/^call.*/)) {
    		e.click();
    		return false;
    	}
    }
  }
  return true;
}

function setImgSrcToInputValue(imgId, inputId) {
	document.getElementById(imgId).src = document.getElementById(inputId).value;
}

function addPrefix(value, prefix) {
	return value.match('^'+prefix+'.*$')? value : prefix + value;
}

function printf(S, L) {
		var nS = "";
		var tS = S.split("%s");
		if (tS.length != L.length+1) throw "Input error";
		for(var i=0; i<L.length; i++)
			nS += tS[i] + L[i];
		return nS + tS[tS.length-1];
}

function debugNode(node) {
	alert("Node type=["+node.nodeType+"] name=["+node.nodeName+"] id=["+node.id+"] tag=["+node.tagName+"] class=["+node.className+"]");
}
function isAnyCheckboxChecked(field) {
	for (i = 0; i < field.length; i++) {
		if(field[i].checked) {
			return true;
		}
	}
	return false;
}
function checkAllCheckboxes(field) {
	for (i = 0; i < field.length; i++) {
		field[i].checked = true ;
	}
}
function uncheckAllCheckboxes(field) {
	for (i = 0; i < field.length; i++) {
		field[i].checked = false ;
	}
}
function setAllCheckboxes(field, value) { 
        if (!field.length) {
                field.checked = value;
        } else {
                for (i = 0; i < field.length; i++) {
                        field[i].checked = value ;
                }
        }
}
function updateForumEntitySubscription(entity, entityId, subscriptionAction, onSuccessFn) {
	  new Ajax.Request('/forums/subscription.html?'+entity+'Id='+entityId+'&action='+subscriptionAction, {
    method: 'get',
    onSuccess: function(transport) {
      var response = transport.responseText || "FAILED";
      if (response == "OK") {
        onSuccessFn();
      } else {
    	  alert('Oops. Failed to update your subscription preference.');
      }
    },
    onFailure: function() {
      alert('Oops. Encountered an error while updating your subscription preference.'); 
    }
  });
  return false;
}
function getPreviousSiblingByTagName(element, tagName) {
  for (var e = element.previousSibling; e != null && e != "undefined"; e = e.previousSibling) {
      if (e.tagName == tagName) {
        return e;
      }
  }
  return null;
}
function getNextSiblingByTagName(element, tagName) {
  for (var e = element.nextSibling; e != null && e != "undefined"; e = e.nextSibling) {
      if (e.tagName == tagName) {
        return e;
      }
  }
  return null;
}

function start_slideshow(frame_name, start_frame, end_frame, delay) {
    setTimeout(switch_slides(frame_name, start_frame, end_frame, delay, start_frame), delay);
}
                            
function switch_slides(frame_name, start_frame, end_frame, delay, current_frame) {
    return (function() {
        Effect.Fade(frame_name + current_frame);
        current_frame = (current_frame == end_frame)? start_frame : current_frame + 1;
        setTimeout("Effect.Appear('" + frame_name + current_frame + "');", 850);
        setTimeout(switch_slides(frame_name, start_frame, end_frame, delay, current_frame), delay + 850);
    });
}
function clearInput(element) {  
    if (!element._cleared) {
        element.value='';
        element._cleared=true;
        return true;
    } else {
        return false;
    }
}
function addHiddenField(form, name, value) {
	var hidden = document.createElement("input");
	hidden.setAttribute("type", "hidden");
	hidden.setAttribute("name", name);
	if (!value) {
		value = 'true';
	}
	hidden.setAttribute("value", value);	
	form.appendChild(hidden);
	return form;
}

/**
 * This function fixes multiple-button-submit issue of IE6 as well as
 * value-submitted-as-label of IE7 and earlier. The solution comes from: from
 * http://www.kopz.org/public/documents/css/multiple_buttons_ie_workaround.html
 */
function buttonFix() {
	var buttons = document.getElementsByTagName('button');
	for ( var i = 0; i < buttons.length; i++) {
		var button = buttons[i];
		if (!button.form || button.form == null || button.type != 'submit') {
			continue;
		}
		if (button.onclick) {
			button.onclickOld = button.onclick;
		}
		button.onclick = function() {
			for ( var j = 0; j < this.form.elements.length; j++) {
				if (this.form.elements[j].tagName == 'BUTTON') {
					this.form.elements[j].disabled = true;
				}
			}
			this.disabled = false;
			var valueAttributeNode = this.attributes.getNamedItem("value");
			if (valueAttributeNode) {
				this.value = valueAttributeNode.nodeValue;
			}
			if (this.onclickOld) {
				return this.onclickOld();
			} else {
				return true;
			}
		}
	}
}
/**
 * This function fixes an issue with IE7 and earlier where image buttons are
 * submitted as coordinates, not their values. The function attaches an onclick
 * handler that dynamically creates a hidden input field that contains the
 * value.
 */
function imageButtonFix() {
	var inputs = document.getElementsByTagName('input');
	for ( var i = 0; i < inputs.length; i++) {
		if (inputs[i].type == 'image') {
			inputs[i].onclickOld = inputs[i].onclick;
			inputs[i].onclick = function(event) {
				addHiddenField(this.form, this.name, this.value);
				if (this.onclickOld) {
					this.onclickOld(event);
				}
			};
		}
	}
}
function randomInt(max) {
	return Math.floor(Math.random() * max);
}
function randomString(length, dashStop) {
	var alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXTZ";
	var out = '';
	for (var i = 1; i <= length; i++) {
		out += (i % 2 == 0)? randomInt(10) : alphabet.charAt(randomInt(alphabet.length));
		if (dashStop && (dashStop > 0) && (i + 1 < length) && (i % dashStop == 0)) {
			out += '-';
		}
	}
	return out;
}

function isEmailAddress(s) {
	return s.match(/^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/);	
}

function setFormSubmitAnchor(element, anchor) {
	var form = element.form;
	var i = form.action.indexOf('#');
	form.action = (i == -1? form.action : form.action.substring(0, i)) + '#' + anchor;
	return true;
}

function checkEmail(targetElement, email) {
	if (email == "" || (targetElement.emailCheck && targetElement.emailCheck == email)) {
		return;
	} else {
		targetElement.emailCheck=email;	
	}
	Element.update(targetElement, ""); 
	new Ajax.Request('/contact-login-check.html',
	{
		method:'post',
		parameters: $H({email: email}).toQueryString(),
		onSuccess: function(transport) {	
			var statusCode = transport.responseText;
			var statusMessage = "";
			var urlPath = window.location.pathname;
			if (window.location.search != null) {
			    urlPath = urlPath + window.location.search;
	        }
			if (statusCode=='OK') {
				i18n(targetElement, 'existingContactRegistrationFailure', urlPath, email);
			} else if (statusCode=='DISABLED') {
				i18n(targetElement, 'disabledContactRegistrationFailure');
			} else if (statusCode=='NO_PASSWORD') {
				i18n(targetElement, 'noPasswordContactRegistrationFailure', urlPath, email);
			} else {
				return;
			}
		},
		onFailure: function() {
			Element.show(targetElement);
			Element.update(targetElement, "Email check failure");
		}
	});
}

function i18n(targetElement, code, arg1, arg2, arg3, arg4, arg5, arg6, arg7) {
	if (targetElement) {
		Element.update(targetElement, "");
	}
	new Ajax.Request('/i18n.html',
	{
		method:'post',
		parameters: $H({code: code, arg1: arg1, arg2: arg2, arg3: arg3, arg4: arg4, arg5: arg5, arg6: arg6, arg7: arg7}).toQueryString(),
		onSuccess: function(transport) {
			if (targetElement) {
				Element.show(targetElement);
				Element.update(targetElement, transport.responseText);
			} else {
				alert(transport.responseText)
			}
		},
		onFailure: function() {
			var msg = "I18N failure: " + code;
			if (targetElement) {
				Element.show(targetElement);
				Element.update(targetElement, msg);
			} else {
				alert(msg);
			}
		}
	});
}

function parseDateTime(s) {
  if (s != null) {
    var match = s.match(/(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2})/);
    if (match != null) {
      return new Date(match[1], match[2]-1, match[3], match[4], match[5]);
    }
  }
  return null;
}

function offsetDate(date, days) {
  return date == null || !days? null : new Date(date.valueOf() + days * 86400000);
}

function getDateOffsetInDays(date1, date2) {
  if (date1 == null || date2 == null) {
	return 0;
  } else {
    return (date1.valueOf() - date2.valueOf()) / 86400000;
  }
}

function formatNumber(number, digits) {
  var s = number.toString();
  while (s.length < digits) {
    s = "0" + s;
  }
  return s;
}

function formatDate(date) {
  return date == null? null : 
    date.getFullYear() + "-" + 
    formatNumber(date.getMonth() + 1, 2) + "-" + 
    formatNumber(date.getDate(), 2); 
}

function formatDateTime(date) {
  return date == null? null : 
	formatDate(date) + " " + 
  	formatNumber(date.getHours(), 2) + ":" + 
  	formatNumber(date.getMinutes(), 2);
}

function enableModalBox() {
	//removing scroll bar 
    $$('HTML')[0].setStyle({overflow: 'hidden'}); 
    //there's no window.outerHeight in IE6 so hard code some value 
    var height = (window.outerHeight) ? window.outerHeight : 2000; 
    $('modal-window-box').setStyle({height : height+'px'}); 
    //hiding select boxes in IE 
    if (Prototype.Browser.IE) {
    	$$('SELECT').invoke('setStyle', {visibility : 'hidden'}) 
    }
    new Effect.Opacity('modal-window-box', { to: 0.7, duration: 0});
}

function disableModalBox() {
	Effect.Fade('modal-window-content');
	Effect.Fade('modal-window-box');
	$$('HTML')[0].setStyle({overflow: 'auto'});
	if (Prototype.Browser.IE) {
    	$$('SELECT').invoke('setStyle', {visibility : 'auto'}) 
    }
}

function acceptEntity(baseElementId, entityId, entityInfo) {
	var openerDoc = window.opener.document;
	var idElement = openerDoc.getElementById(baseElementId+"Input");
	var infoElement = openerDoc.getElementById(baseElementId+"Info");
	var unselectElement = openerDoc.getElementById(baseElementId+"Unselect");
	idElement.value = entityId;
	infoElement.innerHTML = entityInfo.replace(/\n/g, "<br/>\n");
	infoElement.className = 'changedEntityInfo';
	if (idElement.onchange && idElement.onchange != null && idElement.onchange != "") {
		idElement.onchange();
	}
	if (unselectElement) {
		unselectElement.show();
	}
    window.close();
}
function unacceptEntity(baseElementId) {
	var idElement = document.getElementById(baseElementId+"Input");	
	var infoElement = document.getElementById(baseElementId+"Info");
	var viewElement = document.getElementById(baseElementId+"View");
	var extraActionsElement = document.getElementById(baseElementId+"ExtraActions");
	var unselectElement = document.getElementById(baseElementId+"Unselect");
	idElement.value = "";
	infoElement.innerHTML = "";
	viewElement.hide();
	if (extraActionsElement && extraActionsElement != null) {
		extraActionsElement.hide();
	}
	if (unselectElement) {
		unselectElement.hide();
	}
}
function sessionKeepaliveHelper(sessionIdDigest) {
	new Ajax.Request('/session-keepalive', {
	    method: 'post',
	    onSuccess: function(transport) {
		  if (transport.responseText != sessionIdDigest) {
			  window.clearInterval(window.sessionKeepaliveIntervalId);
			  window.sessionKeepaliveIntervalId = null;
			  i18n(null, "expiredSessionMessage");
		  }
	    },
	    onFailure: function() {
	      //ignore
	    }
    });
}
function sessionKeepalive(sessionIdDigest, interval) {
	window.sessionKeepaliveIntervalId =
	  window.setInterval("sessionKeepaliveHelper('"+sessionIdDigest+"')", interval);
}
function httpPost(url, params) {
    var form = document.createElement('form');
    form.action = url;
    form.method = 'POST';
    for (var param in params) {
        if (params.hasOwnProperty(param)) {
            var input = document.createElement('input');
            input.type = 'hidden';
            input.name = param;
            input.value = params[param];
            form.appendChild(input);
        }
    }
    document.body.appendChild(form);
    form.submit();
}
