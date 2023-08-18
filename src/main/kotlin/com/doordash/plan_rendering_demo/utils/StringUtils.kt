package com.doordash.plan_rendering_demo.utils

import com.doordash.rpc.common.UIFlowScreenActionDisplayType
import com.doordash.rpc.common.UIFlowScreenActionIdentifier
import com.doordash.rpc.common.UIFlowScreenActionParameterType
import com.doordash.rpc.common.UIFlowScreenSectionType
import com.doordash.rpc.common.UIFlowScreenTextAlignment

fun String.filterName(): String =
    this.split("\\s".toRegex()).joinToString("_") { it.trim().uppercase() }

fun String.toUIFlowScreenSectionType(): UIFlowScreenSectionType = when (this) {
    "TEXT" -> UIFlowScreenSectionType.TEXT
    "LIST_ITEM_WITH_IMAGE" -> UIFlowScreenSectionType.LIST_ITEM_WITH_IMAGE
    "RADIO_BUTTON" -> UIFlowScreenSectionType.RADIO_BUTTON
    "CENTERED_IMAGE" -> UIFlowScreenSectionType.CENTERED_IMAGE
    "BOLDED_TITLE_TEXT" -> UIFlowScreenSectionType.BOLDED_TITLE_TEXT
    "BULLET_POINT_LIST_ITEM" -> UIFlowScreenSectionType.BULLET_POINT_LIST_ITEM
    "USER_INPUT_TEXT_BOX" -> UIFlowScreenSectionType.USER_INPUT_TEXT_BOX
    "BADGE" -> UIFlowScreenSectionType.BADGE
    "DIVIDER_SPACER" -> UIFlowScreenSectionType.DIVIDER_SPACER
    "DIVIDER_RULER" -> UIFlowScreenSectionType.DIVIDER_RULER
    "HEADER_IMAGE" -> UIFlowScreenSectionType.HEADER_IMAGE
    "TERMS_AND_CONDITIONS" -> UIFlowScreenSectionType.TERMS_AND_CONDITIONS
    "PRICE_LINE_ITEM" -> UIFlowScreenSectionType.PRICE_LINE_ITEM
    "PRICE_LINE_ITEM_HIGHLIGHTED" -> UIFlowScreenSectionType.PRICE_LINE_ITEM_HIGHLIGHTED
    "BANNER" -> UIFlowScreenSectionType.BANNER
    "TEXT_WITH_SEPARATE_LABEL_OR_ACTION" -> UIFlowScreenSectionType.TEXT_WITH_SEPARATE_LABEL_OR_ACTION
    "IMAGE" -> UIFlowScreenSectionType.IMAGE
    "RICH_CARD_RADIO_BUTTON" -> UIFlowScreenSectionType.RICH_CARD_RADIO_BUTTON
    else -> UIFlowScreenSectionType.UNKNOWN_SECTION_TYPE
}

fun String.toUIFlowScreenActionDisplayType(): UIFlowScreenActionDisplayType = when (this) {
    "PRIMARY" -> UIFlowScreenActionDisplayType.PRIMARY
    "TERTIARY" -> UIFlowScreenActionDisplayType.TERTIARY
    "FLAT_SECONDARY" -> UIFlowScreenActionDisplayType.FLAT_SECONDARY
    else -> UIFlowScreenActionDisplayType.UNKNOWN_ACTION_DISPLAY_TYPE
}

fun String.toUIFlowScreenActionParameterType(): UIFlowScreenActionParameterType = when (this) {
    "PLAN_ID" -> UIFlowScreenActionParameterType.PLAN_ID
        "NUMBER_FREE_DAYS" -> UIFlowScreenActionParameterType.NUMBER_FREE_DAYS
    "FREE_DAYS_GRANT_REASON" -> UIFlowScreenActionParameterType.FREE_DAYS_GRANT_REASON
    "DEEPLINK_URL" -> UIFlowScreenActionParameterType.DEEPLINK_URL
    "TRANSITION_TYPE" -> UIFlowScreenActionParameterType.TRANSITION_TYPE
    "TRIAL_ID" -> UIFlowScreenActionParameterType.TRIAL_ID
    "REFUND_TYPE" -> UIFlowScreenActionParameterType.REFUND_TYPE
    "ORDER_UUID" -> UIFlowScreenActionParameterType.ORDER_CART_ID
    "SUCCESS_MESSAGE" -> UIFlowScreenActionParameterType.SUCCESS_MESSAGE
    "NATIVE_PAYMENT_STYLE" -> UIFlowScreenActionParameterType.NATIVE_PAYMENT_STYLE
    "FEE_CURRENCY" -> UIFlowScreenActionParameterType.FEE_CURRENCY
    "COUNTRY_CODE" -> UIFlowScreenActionParameterType.COUNTRY_CODE
    "TRIAL_INTERVAL_UNITS" -> UIFlowScreenActionParameterType.TRIAL_INTERVAL_UNITS
    "TRIAL_INTERVAL_TYPE" -> UIFlowScreenActionParameterType.TRIAL_INTERVAL_TYPE
    "FEE_UNIT_AMOUNT" -> UIFlowScreenActionParameterType.FEE_UNIT_AMOUNT
    "SAVINGS_VALUE" -> UIFlowScreenActionParameterType.SAVINGS_VALUE
    "ENTRY_POINT" -> UIFlowScreenActionParameterType.ENTRY_POINT
    "SHARING_CODE" -> UIFlowScreenActionParameterType.SHARING_CODE
    "DESCRIPTION" -> UIFlowScreenActionParameterType.DESCRIPTION
    "TITLE" -> UIFlowScreenActionParameterType.TITLE
    "STRIPE_TOKEN" -> UIFlowScreenActionParameterType.STRIPE_TOKEN
    "STRIPE_ID" -> UIFlowScreenActionParameterType.STRIPE_ID
    "CONSENT_OBTAINED" -> UIFlowScreenActionParameterType.CONSENT_OBTAINED
    "PAYMENT_METHOD_OPTIONAL" -> UIFlowScreenActionParameterType.PAYMENT_METHOD_OPTIONAL
    "ORDER_CART_ID" -> UIFlowScreenActionParameterType.ORDER_CART_ID
    else -> UIFlowScreenActionParameterType.UNKNOWN_ACTION_PARAMETER_TYPE
}

fun String.toUIFlowScreenTextAlignment(): UIFlowScreenTextAlignment = when (this) {
    "LEFT" -> UIFlowScreenTextAlignment.LEFT
    "RIGHT" -> UIFlowScreenTextAlignment.RIGHT
    "CENTER" -> UIFlowScreenTextAlignment.CENTER
    else -> UIFlowScreenTextAlignment.DEFAULT
}

fun String.toUIFlowScreenActionIdentifier(): UIFlowScreenActionIdentifier = when (this) {
    "GO_BACK" -> UIFlowScreenActionIdentifier.GO_BACK
    "CONTINUE" -> UIFlowScreenActionIdentifier.CONTINUE
    "CANCEL_DASHPASS" -> UIFlowScreenActionIdentifier.CANCEL_DASHPASS
    "SWITCH_TO_MONTHLY_DASHPASS" -> UIFlowScreenActionIdentifier.SWITCH_TO_MONTHLY_DASHPASS
    "GRANT_FREE_DASHPASS" -> UIFlowScreenActionIdentifier.GRANT_FREE_DASHPASS
    "PAUSE_DASHPASS" -> UIFlowScreenActionIdentifier.PAUSE_DASHPASS
    "SUBSCRIBE_TO_DASHPASS" -> UIFlowScreenActionIdentifier.SUBSCRIBE_TO_DASHPASS
    "TRANSITION_SUBSCRIPTION" -> UIFlowScreenActionIdentifier.TRANSITION_SUBSCRIPTION
    "SEND_SHARED_DASHPASS_MEMBERSHIP_LINK" -> UIFlowScreenActionIdentifier.SEND_SHARED_DASHPASS_MEMBERSHIP_LINK
    "INVALIDATE_SHARED_DASHPASS_MEMBERSHIP" -> UIFlowScreenActionIdentifier.INVALIDATE_SHARED_DASHPASS_MEMBERSHIP
    "GO_TO_MANAGE_DASHPASS" -> UIFlowScreenActionIdentifier.GO_TO_MANAGE_DASHPASS
    "GO_TO_RESTAURANT_FEED" -> UIFlowScreenActionIdentifier.GO_TO_RESTAURANT_FEED
    "GO_TO_GROCERY_FEED" -> UIFlowScreenActionIdentifier.GO_TO_GROCERY_FEED
    "GO_TO_HEALTHY_FEED" -> UIFlowScreenActionIdentifier.GO_TO_HEALTHY_FEED
    "GO_TO_PICKUP_FEED" -> UIFlowScreenActionIdentifier.GO_TO_PICKUP_FEED
    "GO_TO_CONVENIENCE_FEED" -> UIFlowScreenActionIdentifier.GO_TO_CONVENIENCE_FEED
    "GO_BACK_TO_ROOT" -> UIFlowScreenActionIdentifier.GO_BACK_TO_ROOT
    "CALL_SUPPORT" -> UIFlowScreenActionIdentifier.CALL_SUPPORT
    "CHAT_WITH_SUPPORT" -> UIFlowScreenActionIdentifier.CHAT_WITH_SUPPORT
    "OPEN_DEEPLINK" -> UIFlowScreenActionIdentifier.OPEN_DEEPLINK
    "GO_TO_HOMEPAGE" -> UIFlowScreenActionIdentifier.GO_TO_HOMEPAGE
    "SELECT_RADIO_BUTTON_ACTION" -> UIFlowScreenActionIdentifier.SELECT_RADIO_BUTTON_ACTION
    else -> UIFlowScreenActionIdentifier.UNKNOWN_ACTION_IDENTIFIER
}