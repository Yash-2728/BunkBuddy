package com.tejasdev.bunkbuddy.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.UI.SubjectViewModel
import com.tejasdev.bunkbuddy.activities.MainActivity
import com.tejasdev.bunkbuddy.databinding.FragmentTimetableBinding
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.datamodel.Subject
import com.tejasdev.bunkbuddy.util.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.tejasdev.bunkbuddy.UI.AlarmViewModel
import com.tejasdev.bunkbuddy.datamodel.HistoryItem
import com.tejasdev.bunkbuddy.util.constants.LECTURE_ADDED
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimetablePresenterFragment : Fragment() {

    private var _binding: FragmentTimetableBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SubjectViewModel
    private var popupWindow: PopupWindow? = null
    private val selectedDayLive = MutableLiveData(0)
    private lateinit var alarmViewModel: AlarmViewModel

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        alarmViewModel = (activity as MainActivity).alarmViewModel
        val list = viewModel.getAllSubjectSync()
        val days = resources.getStringArray(R.array.days).toList()

        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val chipIndex = (today+5)%7

        val adapter = ViewPagerAdapter(requireActivity(), viewModel)


        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(chipIndex, false)
        selectedDayLive.postValue(chipIndex)
        TabLayoutMediator(binding.tabLayout, binding.viewPager){_, _ -> }.attach()

        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                selectedDayLive.postValue(position)
            }
        })

        selectedDayLive.observe(viewLifecycleOwner, Observer {
            binding.selectedDayTv.text = getDay(it)
        })

        binding.addLectureBtn.root.setOnClickListener { showAddLecturePopup(list, days) }

    }


    private fun showAddLecturePopup(subjects: List<Subject>, days: List<String>){
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.add_lecture_popup, null)
        val subjectDropdown: AutoCompleteTextView = popupView.findViewById(R.id.autoCompleteSubject)
        val cancelBtn: Button = popupView.findViewById(R.id.btn_cancel)
        val dayDropdown: AutoCompleteTextView = popupView.findViewById(R.id.autoCompleteDay)
        val addBtn: Button = popupView.findViewById(R.id.btn_add)
        val startTime: TextView = popupView.findViewById(R.id.start_time_tv)
        val endTime: TextView = popupView.findViewById(R.id.end_time_tv)
        val startTimeCard: CardView = popupView.findViewById(R.id.start_time_card)
        val endTimeCard: CardView = popupView.findViewById(R.id.end_time_card)
        val timeAndDate = getDayAndDate()
        startTime.text = timeAndDate[0]
        endTime.text = timeAndDate[0]
        val timepickerLayout = LayoutInflater.from(requireContext()).inflate(R.layout.time_spinner_dialog, null)
        val timepicker: TimePicker = timepickerLayout.findViewById(R.id.time_picker)
        timepicker.setIs24HourView(false)


        val startTimeLive: MutableLiveData<String> = MutableLiveData(timeAndDate[0])
        val endTimeLive: MutableLiveData<String> = MutableLiveData(timeAndDate[0])
        startTimeLive.observe(viewLifecycleOwner, Observer {
            startTime.text = it
        })
        endTimeLive.observe(viewLifecycleOwner, Observer {
            endTime.text = it
        })
        val timepickerWindow by lazy {
            PopupWindow(
                timepickerLayout,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
        }
        timepickerWindow.isFocusable=true

        var startTimePickerVisible = false
        var endTimePickerVisible = false
        startTime.setOnClickListener {
            if(endTimePickerVisible) {
                timepickerWindow.dismiss()
                endTimePickerVisible = false
            }
            startTimePickerVisible = true
            timepickerWindow.showAsDropDown(startTimeCard)
        }
        endTime.setOnClickListener {
            if(startTimePickerVisible) {
                timepickerWindow.dismiss()
                startTimePickerVisible = false
            }
            endTimePickerVisible = true
            timepickerWindow.showAsDropDown(endTimeCard)
        }
        timepicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            val aMPM = if(hourOfDay>11) "PM" else "AM"
            var hour = hourOfDay
            if(hour>11) hour %= 12
            if(hour==0) hour=12
            var min = minute.toString()
            if(minute<10) min="0$min"
            if(endTimePickerVisible){
                endTimeLive.postValue("${hour}:${min} $aMPM")
            }
            else if(startTimePickerVisible){
                startTimeLive.postValue("${hour}:${min} $aMPM")
            }
            else {
                Toast.makeText(requireContext(), "states: $startTimePickerVisible $endTimePickerVisible", Toast.LENGTH_SHORT).show()
            }
        }


        addBtn.isEnabled = false
        addBtn.setTextColor(resources.getColor(R.color.text_primary))

        var subject: Subject? = null
        var day: Int? = null
        val subjectNames = subjects.map { it.name }

        val subjectAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, subjectNames)
        subjectDropdown.setAdapter(subjectAdapter)
        subjectDropdown.setOnItemClickListener { _, _, position, _ ->
            subject = subjects[position]
            addBtn.isEnabled = (day!=null && subject!=null)
            if(addBtn.isEnabled) addBtn.setTextColor(resources.getColor(R.color.primary_blue))
            else addBtn.setTextColor(resources.getColor(R.color.text_primary))
        }

        val daysAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, days)
        dayDropdown.setAdapter(daysAdapter)
        dayDropdown.setOnItemClickListener { _, _, position, _ ->
            day = position
            addBtn.isEnabled = (day!=null && subject!=null)
            if(addBtn.isEnabled) addBtn.setTextColor(resources.getColor(R.color.primary_blue))
            else addBtn.setTextColor(resources.getColor(R.color.text_primary))
        }

        addBtn.setOnClickListener {
            Log.w("bunkbuddyerrorlogs" , "$day $subject ${startTimeLive.value} ${endTimeLive.value}")
            addLecture(day!!, subject!!,startTimeLive.value!! ,endTimeLive.value!!)
            popupWindow?.dismiss()
        }
        cancelBtn.setOnClickListener {
            popupWindow?.dismiss()
        }

        popupWindow = PopupWindow(
            popupView,
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        popupWindow?.isFocusable=true
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.pop_up_in_anim)
        popupView.startAnimation(animation)
        popupWindow?.showAtLocation(popupView, Gravity.BOTTOM, 0, 0)
    }

    private fun getDayAndDate():List<String> {
        val currentDate = Calendar.getInstance().time

        return listOf(
            SimpleDateFormat("hh:mm a", Locale.US).format(currentDate),
            SimpleDateFormat("d MMM yyyy", Locale.US).format(currentDate)
        )
    }
    private fun addLecture(
        dayNumber:Int,
        subject: Subject,
        start: String,
        end: String
    ){
        val lecture = Lecture(dayNumber, subject, start, end, 0, subject.id)
        val id = viewModel.addLecture(lecture)
        lecture.pid = id
        val notificationEnabled = (activity as MainActivity).isNotificationEnabled
        if(notificationEnabled){
            alarmViewModel.setAlarm(lecture)
        }
        val dayAndDate = getDayAndDate()
        val historyItem = HistoryItem(
            LECTURE_ADDED,
            String.format(
                requireContext().getString(
                    R.string.added_lecture
                ),
                lecture.subject.name,
                getDay(lecture.dayNumber),
                lecture.startTime,
                lecture.endTime
            ),
            time = dayAndDate[0],
            date = dayAndDate[1]
        )
        viewModel.addHistory(historyItem)
    }
    private fun getDay(num: Int): String{
        return when(num){
            0-> requireContext().getString(R.string.monday)
            1-> requireContext().getString(R.string.tuesday)
            2-> requireContext().getString(R.string.wednesday)
            3-> requireContext().getString(R.string.thursday)
            4-> requireContext().getString(R.string.friday)
            5-> requireContext().getString(R.string.saturday)
            else -> requireContext().getString(R.string.sunday)
        }
    }
}