package org.sirekanyan.rknfreebot

const val OVPN_EXTENSION = ".ovpn"
const val LOCATION_DELIMITER = "-"

fun createResources(lang: String?): Resources =
    when (lang) {
        "ru" -> RuResources()
        else -> EnResources()
    }

sealed class Resources {
    abstract val shareButton: String
    abstract val shareText: String
    abstract val helloMessage: String
    abstract val welcomeMessage: String
    abstract val notAuthorized: String
    abstract val invitationExpired: String
    abstract val emptyKeysForLocation: String
    abstract val emptyLocations: String
    abstract val help: String
}

class EnResources : Resources() {
    override val shareButton = "Share"
    override val shareText = "free vpn for friends"
    override val helloMessage = "Select server location. Note that location near to you may be the best choice."
    override val notAuthorized = "You're not invited yet. Ask to other users to send you an invitation code."
    override val welcomeMessage = "Hi and welcome! Select server location."
    override val invitationExpired = "Invitation code was expired, ask to your friend for another one."
    override val emptyKeysForLocation = "There are no available keys in this location. Try another one."
    override val emptyLocations = "There are no available keys at the moment. Please try later."
    override val help = "1. Install *OpenVPN Connect* " +
            "for [Android](https://play.google.com/store/apps/details?id=net.openvpn.openvpn) " +
            "or [iOS](https://itunes.apple.com/us/app/openvpn-connect/id590379981?mt=8)\n" +
            "2. Launch *OpenVPN Connect* and import ovpn profile (ex: Fraknfurt1.ovpn)"
}

class RuResources : Resources() {
    override val shareButton = "Поделиться"
    override val shareText = "випиэн для друзей"
    override val helloMessage = "Выберите местоположение сервера. Чем ближе к вам находится сервер, тем лучше."
    override val notAuthorized = "Вы пока не приглашены. Попросите пользователей этого бота выслать вам приглашение."
    override val welcomeMessage = "Добро пожаловать! Выберите местоположение сервера."
    override val invitationExpired = "Код приглашения устарел или неверный, попросите выслать его повторно."
    override val emptyKeysForLocation = "Нет доступных ключей для этого местоположения. Попробуйте другие варианты."
    override val emptyLocations = "Талонов нет. Попробуйте позже."
    override val help = "1. Установите *OpenVPN Connect* " +
            "для [Android](https://play.google.com/store/apps/details?id=net.openvpn.openvpn) " +
            "или [iOS](https://itunes.apple.com/us/app/openvpn-connect/id590379981?mt=8)\n" +
            "2. Запустите приложение *OpenVPN Connect* и импортируйте файл профиля (например, Fraknfurt1.ovpn)"
}
