package io.duna.eventbus.message

sealed class TransmissionMode

object Unicast extends TransmissionMode
object Broadcast extends TransmissionMode
