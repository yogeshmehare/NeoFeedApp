package com.yogesh.neofeedapp.view

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate.JOYFUL_COLORS
import com.github.mikephil.charting.utils.ColorTemplate.createColors
import com.yogesh.neofeedapp.R
import com.yogesh.neofeedapp.databinding.FragmentNeoFeedBinding
import com.yogesh.neofeedapp.model.`interface`.RetrofitService
import com.yogesh.neofeedapp.model.models.Asteroid
import com.yogesh.neofeedapp.model.models.MainRepository
import com.yogesh.neofeedapp.model.models.MyDatePicker
import com.yogesh.neofeedapp.viewmodel.NeoFeedViewModel
import com.yogesh.neofeedapp.viewmodel.NeoFeedViewModelFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class NeoFeedFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var viewModel: NeoFeedViewModel
    private lateinit var binding : FragmentNeoFeedBinding
    private var selectedDate : String = ""
    var startButtonClicked = true

    var lineChart: LineChart? = null
    var lineData: LineData? = null
    var lineDataSet: LineDataSet? = null
    var lineEntries: ArrayList<Entry> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_neo_feed,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofitService = RetrofitService.getInstance()
        val repository = MainRepository(retrofitService)
        viewModel = ViewModelProvider(this, factory = NeoFeedViewModelFactory(repository))[NeoFeedViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.asteroids.observe(viewLifecycleOwner){
            if (it.isNotEmpty()) {
                Log.d("NeoApp",it.toString())
                viewModel.fastest_asteroid.value = getFastestAsteroid(it)
                viewModel.closest_asteroid.value = getClosestAsteroid(it)
                viewModel.average_size_of_asteroid.value = averageSizeOfAsteroid(it)
                setLineChartData()
            }
        }


        binding.buttonStartDate.setOnClickListener {
            startButtonClicked = true
            val mDatePickerDialogFragment = MyDatePicker()
            mDatePickerDialogFragment.show(childFragmentManager, "DATE PICK")
        }

        binding.buttonEndDate.setOnClickListener {
            startButtonClicked = false
            val mDatePickerDialogFragment = MyDatePicker()
            mDatePickerDialogFragment.show(childFragmentManager, "DATE PICK")
        }

        setLineChartData()
//
//        viewModel.loading.observe(viewLifecycleOwner) {
//            if (it) {
//                binding.progressBar.visibility = View.VISIBLE
//                Log.d("Retrofit", viewModel.quotesList.value?.size.toString())
//            } else {
//                binding.progressBar.visibility = View.GONE
//            }
//        }
    }

    private fun configureLineChart() {
        val desc = Description()
        desc.text = "Stock Price History"
        desc.textSize = 28F
        binding.activityMainLinechart.description = desc
        val xAxis: XAxis = binding.activityMainLinechart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            private val mFormat: SimpleDateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
            override fun getFormattedValue(value: Float): String {
                val millis = value.toLong() * 1000L
                return mFormat.format(Date(millis))
            }
        }
    }

    private fun getFastestAsteroid(list: List<Asteroid>): String {
        var fastest = Float.NEGATIVE_INFINITY
        lateinit var fastestAsteroid : Asteroid
        for (i in list){
            if (i.relativeVelocity>fastest){
                fastest = i.relativeVelocity.toFloat()
                fastestAsteroid = i
            }
        }
        return fastestAsteroid.id.toString() + " " + fastestAsteroid.relativeVelocity.toString()
    }

    private fun getClosestAsteroid(list: List<Asteroid>): String {
        var minDistanceFromEarth = Float.POSITIVE_INFINITY
        lateinit var closestAsteroid : Asteroid
        for (i in list){
            if (i.distanceFromEarth<minDistanceFromEarth){
                minDistanceFromEarth = i.distanceFromEarth.toFloat()
                closestAsteroid = i
            }
        }
        return closestAsteroid.id.toString() + " " + closestAsteroid.distanceFromEarth.toString()
    }

    private fun averageSizeOfAsteroid(list: List<Asteroid>): String {
        var avgSize = 0.0
        for (i in list){
            avgSize += i.estimatedDiameter
        }
        avgSize /= list.size
        return avgSize.toString()
    }

    fun setLineChartData() {
        val linevalues = ArrayList<Entry>()

        var i = 1
        for(item in MainRepository.dateListAsteroids){
            linevalues.add(Entry(i.toFloat(), item.value.toFloat()))
            i++
        }

        val linedataset = LineDataSet(linevalues, "First")
        //We add features to our chart
        linedataset.color = resources.getColor(R.color.purple_200)

        linedataset.circleRadius = 10f
        linedataset.setDrawFilled(true)
        linedataset.valueTextSize = 20F
        linedataset.fillColor = resources.getColor(R.color.purple_200)
        linedataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        //We connect our data to the UI Screen
        val data = LineData(linedataset)
        binding.activityMainLinechart.data = data
        binding.activityMainLinechart.setBackgroundColor(resources.getColor(R.color.white))
        binding.activityMainLinechart.animateXY(2000, 2000, Easing.EaseInCubic)

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val mCalendar: Calendar = Calendar.getInstance()
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, month)
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.time)

        var selectedMonth=""
        var selectedDayOfMonth=""
        if(month < 10){
            selectedMonth = "0$month";
        }else{
            selectedMonth = month.toString()
        }
        if(dayOfMonth < 10){
            selectedDayOfMonth  = "0$dayOfMonth";
        }else{
            selectedDayOfMonth = dayOfMonth.toString()
        }

        if (startButtonClicked)
            viewModel.start_date.value = "$year-$selectedMonth-$selectedDayOfMonth"
        else
            viewModel.end_date.value = "$year-$selectedMonth-$selectedDayOfMonth"
    }

}