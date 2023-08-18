function onScreenItemChanged() {
    let elementType = document.getElementById('elementType').value
    let elementFormat = document.getElementById('elementFormat')
    let elementFormatValue = getElementFormat(elementType)
    elementFormat.innerHTML = '<pre>' + elementFormatValue + '</pre>'
}

function getElementFormat(elementType) {
    switch(elementType) {
        case "CENTERED_IMAGE":
            return '  {\n' +
                '    "type": "' + elementType + '",\n' +
                '    "content": "ID of registered image URL string"\n' +
            '  }'

        case "LIST_ITEM_WITH_IMAGE":
            return '{\n' +
                '    "type": "' + elementType + '",\n' +
                '    "content": "image URL",\n' +
                '    "action_label": "ID of registered action label string",\n' +
                '    "action_display_type": "(PRIMARY|TERTIARY|FLAT_SECONDARY)",\n' +
                '    "action_parmeter_type": "(PLAN_ID|NUMBER_FREE_DAYS|FREE_DAYS_GRANT_REASON|DEEPLINK_URL|TRANSITION_TYPE|' +
                '        TRIAL_ID|REFUND_TYPE|ORDER_UUID|SUCCESS_MESSAGE|NATIVE_PAYMENT_STYLE|' +
                '        FEE_CURRENCY|COUNTRY_CODE|TRIAL_INTERVAL_UNITS|TRIAL_INTERVAL_TYPE|FEE_UNIT_AMOUNT|SAVINGS_VALUE|' +
                '         ENTRY_POINT|SHARING_CODE|DESCRIPTION|TITLE|STRIPE_TOKEN|' +
                '         STRIPE_ID|CONSENT_OBTAINED|PAYMENT_METHOD_OPTIONAL|ORDER_CART_ID)",\n' +
                '    "action_parameter_value": "parameter value"\n' +
                '  }'

        case "RADIO_BUTTON":
        case "USER_INPUT_TEXT_BOX":
            return '  {\n' +
                '    "type": "' + elementType + '",\n' +
                '    "content": "ID of registered string"\n' +
                '  }'

        case "DIVIDER_RULER":
        case "DIVIDER_SPACER":
            return '  {\n' +
                '    "type": "' + elementType + '"\n' +
                '  }'

        case "HEADER_IMAGE":
            return '  {\n' +
                '    "type": "' + elementType + '",\n' +
                '    "content": "[ID of registered header image URL, ...]"\n' +
                '  }'

        case "BANNER":
        case "TEXT_WITH_SEPARATE_LABEL_OR_ACTION":
            return '  {\n' +
                '    "type": "' + elementType + '",\n' +
                '    "content": "[ID of registered string, ...]"\n' +
                '  }'

        case "IMAGE":
            return '  {\n' +
                '    "type": "' + elementType + '",\n' +
                '    "content": "[ID of registered image url, ...]",\n' +
                '    "alignment":"(LEFT|RIGHT|CENTER|DEFAULT)"\n' +
                '  }'

        case "RICH_CARD_RADIO_BUTTON":
            return '  {\n' +
                '    "type": "' + elementType + '",\n' +
                '    "content": "[ID of registered string, ...]",\n' +
                '    "alignment":"(LEFT|RIGHT|CENTER|DEFAULT)",\n' +
                '    "action": {\n' +
                '        "label": "{ID of registered string}",\n' +
                '        "type": "(GO_BACK|CONTINUECANCEL_DASHPASS|SWITCH_TO_MONTHLY_DASHPASS|GRANT_FREE_DASHPASS|\n' +
                '            PAUSE_DASHPASS|SUBSCRIBE_TO_DASHPASS|TRANSITION_SUBSCRIPTION|\n' +
                '            SEND_SHARED_DASHPASS_MEMBERSHIP_LINK|INVALIDATE_SHARED_DASHPASS_MEMBERSHIP|\n' +
                '            GO_TO_MANAGE_DASHPASS|GO_TO_RESTAURANT_FEED|GO_TO_GROCERY_FEED|\n' +
                '            GO_TO_HEALTHY_FEED|GO_TO_PICKUP_FEED|GO_TO_CONVENIENCE_FEED|\n' +
                '            GO_BACK_TO_ROOT|CALL_SUPPORT|CHAT_WITH_SUPPORT|OPEN_DEEPLINK|\n' +
                '            GO_TO_HOMEPAGE|SELECT_RADIO_BUTTON_ACTION)",\n' +
                '        "display_type": "(PRIMARY|TERTIARY|FLAT_SECONDARY)",\n' +
                '        "parameters": [\n' +
                '            {\n' +
                '              "key": "(PLAN_ID|NUMBER_FREE_DAYS|FREE_DAYS_GRANT_REASON|DEEPLINK_URL|\n' +
                '                  TRANSITION_TYPE|TRIAL_ID|REFUND_TYPE|ORDER_UUID|SUCCESS_MESSAGE|\n' +
                '                  NATIVE_PAYMENT_STYLE|FEE_CURRENCY|COUNTRY_CODE|TRIAL_INTERVAL_UNITS|\n' +
                '                  TRIAL_INTERVAL_TYPE|FEE_UNIT_AMOUNT|SAVINGS_VALUE|ENTRY_POINT|\n' +
                '                  SHARING_CODE|DESCRIPTION|TITLE|STRIPE_TOKEN|STRIPE_ID|\n' +
                '                  CONSENT_OBTAINED|PAYMENT_METHOD_OPTIONAL|ORDER_CART_ID)",\n' +
                '              "value": "{ID of registered string}"\n' +
                '            }\n' +
                '          ],\n' +
                '        "post_action": "(GO_BACK|CONTINUE|CANCEL_DASHPASS|SWITCH_TO_MONTHLY_DASHPASS|\n' +
                '           GRANT_FREE_DASHPASS|PAUSE_DASHPASS|SUBSCRIBE_TO_DASHPASS|TRANSITION_SUBSCRIPTION|\n'+
                '           SEND_SHARED_DASHPASS_MEMBERSHIP_LINK|INVALIDATE_SHARED_DASHPASS_MEMBERSHIP|\n' +
                '           GO_TO_MANAGE_DASHPASS|GO_TO_RESTAURANT_FEED|GO_TO_GROCERY_FEED|GO_TO_HEALTHY_FEED|\n' +
                '           GO_TO_PICKUP_FEED|GO_TO_CONVENIENCE_FEED|GO_BACK_TO_ROOT|CALL_SUPPORT|\n' +
                '           CHAT_WITH_SUPPORT|OPEN_DEEPLINK|GO_TO_HOMEPAGE|SELECT_RADIO_BUTTON_ACTION)"\n' +
                '      },\n' +
                '    "rich_content": [\n' +
                '        {\n' +
                '          "content": "{ID of registered string}",\n' +
                '          "format_type": "(TYPE_BADGE|TYPE_TEXT|TYPE_BANNER)",\n' +
                '          "format_color": "(COLOR_HIGHLIGHT|COLOR_PRIMARY|COLOR_SECONDARY|COLOR|TERTIARY|COLOR_DISABLED)",\n' +
                '          "alignment": "(LEFT|RIGHT|CENTER|DEFAULT)"\n' +
                '        }\n' +
                '      ]\n' +
                '  }'

        default:
            return '  {\n' +
                '    "type": "' + elementType + '",\n' +
                '    "content": "ID of registered string",\n' +
                '    "alignment":"(LEFT|RIGHT|CENTER|DEFAULT)"\n' +
                '  }'
    }
}