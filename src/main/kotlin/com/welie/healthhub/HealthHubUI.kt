package com.welie.healthhub

import com.welie.healthhub.observations.Observation
import com.welie.healthhub.observations.ObservationType
import com.welie.healthhub.observations.QuantityType
import com.welie.healthhub.observations.SystemInfoStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.swing.JFrame
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Container
import java.util.*

import javax.swing.JScrollPane

import javax.swing.text.SimpleAttributeSet

import javax.swing.text.StyleConstants

import javax.swing.JTextPane
import javax.swing.SwingUtilities
import javax.swing.text.StyledDocument


class HealthHubUI(bluetoothHandler: BluetoothHandler) : DataCallback {

    private val logger: Logger = LoggerFactory.getLogger("HealthHubUI")
    private val overallStyles = SimpleAttributeSet()
    private val valueStyles = SimpleAttributeSet()
    private val unitStyles = SimpleAttributeSet()
    private var pane: JTextPane

    init {
        val frame = JFrame("Health Hub")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
//        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        frame.setUndecorated(true);
//        frame.setVisible(true);

        val cp: Container = frame.contentPane
        pane = JTextPane()
        pane.isEditable = false

        StyleConstants.setBold(overallStyles, true)
        StyleConstants.setAlignment(overallStyles, StyleConstants.ALIGN_CENTER)
        StyleConstants.setSpaceAbove(overallStyles, 32.0f)
        StyleConstants.setForeground(overallStyles, Color(0, 100, 30))

        StyleConstants.setFontFamily(valueStyles, "Ubuntu")
        StyleConstants.setFontSize(valueStyles, 64)
        StyleConstants.setBold(valueStyles, true)

        StyleConstants.setFontFamily(unitStyles, "Ubuntu")
        StyleConstants.setFontSize(unitStyles, 32)
        StyleConstants.setBold(unitStyles, false)

        val scrollPane = JScrollPane(pane)
        cp.add(scrollPane, BorderLayout.CENTER)

        frame.setSize(550, 350)
        frame.isVisible = true

        updateValue("--", "--", "")
        bluetoothHandler.setDataCallback(this)
        bluetoothHandler.setFrame(frame)
    }

    private fun updateValue(value: String, unit: String, timestamp: String) {
        SwingUtilities.invokeLater {
            val doc: StyledDocument = pane.styledDocument
            pane.setCharacterAttributes(valueStyles, true)
            pane.text = value
            pane.setCharacterAttributes(unitStyles, true)
            doc.insertString(doc.length, " $unit\n$timestamp", unitStyles)
            doc.setParagraphAttributes(0, doc.length, overallStyles, false)
        }
    }

    override fun onObservationList(observationList: List<Observation>) {
        logger.info(observationList.toString())
        val quantityTypes = observationList.map { it.quantityType }
        val displaybleTypes = setOf(QuantityType.Saturation, QuantityType.Concentration, QuantityType.Frequency, QuantityType.Mass, QuantityType.Temperature)

        // Handle observation list
        if (quantityTypes.contains(QuantityType.SystolicPressure) && quantityTypes.contains(QuantityType.DiastolicPressure)) {
            val systolic = requireNotNull(observationList.find { it.quantityType == QuantityType.SystolicPressure })
            val diastolic = requireNotNull(observationList.find { it.quantityType == QuantityType.DiastolicPressure })
            updateValue(
                String.format("%.0f/%.0f", systolic.value, diastolic.value),
                systolic.unit.notation,
                systolic.timestamp.toString()
            )
        } else {
            observationList.forEach {
                if (displaybleTypes.contains(it.quantityType)) {
                    showObservation(it)
                }
            }
        }
    }

    override fun onBatteryPercentage(percentage: Int, systemId: String) {
        logger.info("Battery percentage $percentage")
        SystemInfoStore.get(systemId)?.batteryLevel = percentage
    }

    override fun onPeripheralTime(dateTime: Date, systemId: String) {
        SystemInfoStore.get(systemId)?.dateTime = dateTime
    }

    override fun onManufacturerName(manufacturer: String, systemId: String) {
        logger.info("Manufacturer name: $manufacturer")
        SystemInfoStore.get(systemId)?.manufacturer = manufacturer
    }

    override fun onModelNumber(modelNumber: String, systemId: String) {
        logger.info("Model number $modelNumber")
        SystemInfoStore.get(systemId)?.model = modelNumber
    }

    override fun onSerialNumber(serialNumber: String, systemId: String) {
        SystemInfoStore.get(systemId)?.serialNumber = serialNumber
    }

    override fun onFirmwareRevision(firmwareRevision: String, systemId: String) {
        SystemInfoStore.get(systemId)?.firmwareVersion = firmwareRevision
    }

    override fun onHardwareRevision(hardwareRevision: String, systemId: String) {
        SystemInfoStore.get(systemId)?.hardwareVersion = hardwareRevision
    }

    override fun onSoftwareRevision(softwareRevision: String, systemId: String) {
        SystemInfoStore.get(systemId)?.softwareVersion = softwareRevision
    }

    private fun showObservation(observation: Observation) {
        with(observation) {
            updateValue(String.format("%.1f", value), unit.notation, timestamp.toString())
        }
    }
}