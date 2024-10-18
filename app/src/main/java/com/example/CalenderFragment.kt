package com.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Adapter.EventAdapter
import com.example.Adapter.LessonAdapter
import com.example.Adapter.ProgramAdapter
import com.example.Adapter.TestListAdapter
import com.example.model.EventsModel
import com.example.model.LessonModel
import com.example.model.ProgramsModel
import com.example.model.TestListModel
import com.example.trainerapp.ApiClass.APIClient
import com.example.trainerapp.ApiClass.APIInterface
import com.example.trainerapp.PreferencesManager
import com.example.trainerapp.SignInActivity
import com.example.trainerapp.databinding.FragmentCalenderBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class CalenderFragment : Fragment() {
    lateinit var apiInterface: APIInterface
    lateinit var apiClient: APIClient
    lateinit var preferencesManager: PreferencesManager

    private lateinit var calenderBinding: FragmentCalenderBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        calenderBinding = FragmentCalenderBinding.inflate(layoutInflater)

        val calender = Calendar.getInstance()
//        val year = calender.get(Calendar.YEAR)
//        val month = calender.get(Calendar.MONTH)
//        val day = calender.get(Calendar.DAY_OF_MONTH)

        calenderBinding.frgCalendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val date = (year.toString() + "-" + (month + 1) + "-" + dayOfMonth)
            calenderBinding.dateTv.setText(date).toString()
        }


//        val daysOfWeek = daysOfWeekFromLocale()
//        val currentMonth = YearMonth.now()
//        calenderView.apply {
//            setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first())
//            scrollToMonth(currentMonth)
//        }

        preferencesManager = PreferencesManager(requireContext())
        apiClient = APIClient(requireContext())
        apiInterface = apiClient.client().create(APIInterface::class.java)
        val data: MutableMap<String, String> = HashMap()
//        data["date"] = date_tv.toString()
        data["date"] = "2022-09-6"

        apiInterface.GetDateEvent(data)?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(
                call: Call<JsonObject?>,
                response: Response<JsonObject?>
            ) {
                val code = response.code()
                if (code == 200) {
                    Log.e("calender", response.body().toString() + "")
                    val jsonObject = JSONObject(Gson().toJson(response.body()))
                    val res = jsonObject.getString("status")
                    if (res.equals("true")) {
                        val data = jsonObject.getJSONObject("data")
                        val list = data.getJSONObject("list")
                        val lessons = list.getJSONArray("lessons")
                        var lesson = ArrayList<LessonModel>()
                        for (i in 0 until lessons.length()) {
                            val lessons: LessonModel = Gson().fromJson(
                                lessons.getString(i).toString(), LessonModel::class.java
                            )
                            lesson.add(lessons)
                        }
                        val Sub = LessonAdapter(requireContext(), lesson)
                        val linearLayoutManager = LinearLayoutManager(requireContext())
                        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                        calenderBinding.rcvlesson.layoutManager = linearLayoutManager
                        calenderBinding.rcvlesson.setHasFixedSize(true)
                        calenderBinding.rcvlesson.itemAnimator = DefaultItemAnimator()
                        calenderBinding.rcvlesson.adapter = Sub

                        //programs//
                        val programs = list.getJSONArray("programs")
                        Log.e("programdata", programs.toString())
                        var program = ArrayList<ProgramsModel>()
                        for (i in 0 until programs.length()) {
                            val programs: ProgramsModel = Gson().fromJson(
                                programs.getString(i).toString(), ProgramsModel::class.java
                            )
                            program.add(programs)
                        }
                        val pro = ProgramAdapter(requireContext(), program)
                        val linearLayoutManager1 = LinearLayoutManager(requireContext())
                        linearLayoutManager1.orientation = LinearLayoutManager.VERTICAL
                        calenderBinding.rcvprogram.layoutManager = linearLayoutManager1
                        calenderBinding.rcvprogram.setHasFixedSize(true)
                        calenderBinding.rcvprogram.itemAnimator = DefaultItemAnimator()
                        calenderBinding.rcvprogram.adapter = pro

                        //event//

                        val event = list.getJSONArray("events")
                        Log.e("eventdata", programs.toString())
                        var eventmodel = ArrayList<EventsModel>()
                        for (i in 0 until event.length()) {
                            val eventss: EventsModel = Gson().fromJson(
                                event.getString(i).toString(), EventsModel::class.java
                            )
                            eventmodel.add(eventss)
                        }
                        val eventadapter = EventAdapter(requireContext(), eventmodel)
                        val linearLayoutevent = LinearLayoutManager(requireContext())
                        linearLayoutevent.orientation = LinearLayoutManager.VERTICAL
                        calenderBinding.rcvevent.layoutManager = linearLayoutevent
                        calenderBinding.rcvevent.setHasFixedSize(true)
                        calenderBinding.rcvevent.itemAnimator = DefaultItemAnimator()
                        calenderBinding.rcvevent.adapter = eventadapter
                        //test//
                        val test = list.getJSONArray("tests")
                        Log.e("testdata", programs.toString())
                        var testmodel = ArrayList<TestListModel>()
                        for (i in 0 until test.length()) {
                            val testlist: TestListModel = Gson().fromJson(
                                test.getString(i).toString(), TestListModel::class.java
                            )
                            testmodel.add(testlist)
                        }
                        if (testmodel.isNotEmpty()) {
                            val testListAdapter = TestListAdapter(requireContext(), testmodel)
                            val testlayout = LinearLayoutManager(requireContext())
                            testlayout.orientation = LinearLayoutManager.VERTICAL
                            calenderBinding.rcvtest.layoutManager = testlayout
                            calenderBinding.rcvtest.setHasFixedSize(true)
                            calenderBinding.rcvtest.itemAnimator = DefaultItemAnimator()
                            calenderBinding.rcvtest.adapter = testListAdapter
                        }
                    } else {

                    }
                } else if (response.code() == 403) {
                    val message = response.message()
                    Toast.makeText(
                        requireContext(),
                        "" + message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    call.cancel()
                    startActivity(
                        Intent(
                            requireContext(),
                            SignInActivity::class.java
                        )
                    )
                    requireActivity().finish()
                } else {
                    val message = response.message()
                    Toast.makeText(requireContext(), "" + message, Toast.LENGTH_SHORT)
                        .show()
                    call.cancel()
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                Toast.makeText(requireContext(), "" + t.message, Toast.LENGTH_SHORT)
                    .show()
                call.cancel()
            }
        })

        return calenderBinding.root
    }

}