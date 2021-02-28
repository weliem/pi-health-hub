package com.welie.healthhub

import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.gatt.*
import com.welie.healthhub.observations.ObservationUnit
import com.welie.healthhub.observations.Observation
import com.welie.healthhub.observations.ObservationType
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
        val observationTypes = observationList.map { it.type }

        // Handle observation list
        if (observationTypes.contains(ObservationType.SystolicCuffPressure) && observationTypes.contains(ObservationType.DiastolicCuffPressure)) {
            val systolic = requireNotNull(observationList.find { it.type == ObservationType.SystolicCuffPressure })
            val diastolic = requireNotNull(observationList.find { it.type == ObservationType.DiastolicCuffPressure })
            updateValue(
                String.format("%.0f/%.0f", systolic.value, diastolic.value),
                systolic.unit.notation,
                systolic.timestamp.toString()
            )
        }

        if (observationTypes.contains(ObservationType.Temperature)) {
            val temperature = requireNotNull(observationList.find {it.type == ObservationType.Temperature})
            showObservation(temperature)
        }

        if (observationTypes.contains(ObservationType.BodyWeight)) {
            val weight = requireNotNull(observationList.find {it.type == ObservationType.BodyWeight})
            showObservation(weight)
        }

        if (observationTypes.contains(ObservationType.BloodOxygen)) {
            val spO2 = requireNotNull(observationList.find {it.type == ObservationType.BloodOxygen})
            showObservation(spO2)
        }

        if (observationTypes.contains(ObservationType.BloodGlucose)) {
            val glucose = requireNotNull(observationList.find {it.type == ObservationType.BloodGlucose})
            showObservation(glucose)
        }
    }

    override fun onBatteryPercentage(percentage: Int, systemId: String) {
        logger.info("Battery percentage $percentage")
    }

    override fun onPeripheralTime(dateTime: Date, systemId: String) {
        TODO("Not yet implemented")
    }

    override fun onManufacturerName(manufacturer: String, systemId: String) {
        logger.info("Manufacturer name: $manufacturer")
    }

    override fun onModelNumber(modelNumber: String, systemId: String) {
        logger.info("Model number $modelNumber")
    }

    private fun showObservation(observation: Observation) {
        with(observation) {
            updateValue(String.format("%.1f", value), unit.notation, timestamp.toString())
        }
    }
}