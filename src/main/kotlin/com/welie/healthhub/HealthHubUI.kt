package com.welie.healthhub

import com.welie.blessed.BluetoothPeripheral
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

    override fun onTemperature(measurement: TemperatureMeasurement, peripheral: BluetoothPeripheral) {
        with(measurement) {
            updateValue(
                String.format("%.1f", temperatureValue),
                if (unit == TemperatureUnit.Celsius) "\u00B0C" else "\u00B0F",
                timestamp.toString()
            )
        }
    }

    override fun onBloodPressure(measurement: BloodPressureMeasurement, peripheral: BluetoothPeripheral) {
        with(measurement) {
            updateValue(
                String.format("%.0f/%.0f", systolic, diastolic),
                if (unit == BloodPressureUnit.MMHG) "mmHg" else "kPa",
                timestamp.toString()
            )
        }
    }

    override fun onWeight(measurement: WeightMeasurement, peripheral: BluetoothPeripheral) {
        with(measurement) {
            updateValue(
                String.format("%.1f", weight),
                if (unit == WeightUnit.Kilograms) "Kg" else "lbs",
                timestamp.toString()
            )
        }
    }

    override fun onHeartRate(measurement: HeartRateMeasurement, peripheral: BluetoothPeripheral) {
        with(measurement) {
            updateValue(pulse.toString(), "bpm", "")
        }
    }

    override fun onBloodOxygen(measurement: PulseOximeterSpotMeasurement, peripheral: BluetoothPeripheral) {
        with(measurement) {
            updateValue(String.format("%.0f", spO2), "%", timestamp.toString())
        }
    }

    override fun onAirPressure(pressure: Float, peripheral: BluetoothPeripheral) {
        updateValue(String.format("%.2f", pressure), "hPa", Calendar.getInstance().time.toString())
    }

    override fun onBloodGlucose(measurement: GlucoseMeasurement, peripheral: BluetoothPeripheral) {
        with(measurement) {
            updateValue(
                String.format("%.1f", value),
                if (unit == GlucoseMeasurementUnit.MmolPerLiter) "mmol/L" else "mg/Dl",
                timestamp.toString()
            )
        }
    }
}