package com.diegoparra.kinodb.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class FavouritesDaoTest {

    private val testDispatcher = TestCoroutineScope()
    private lateinit var database: KinoDatabase
    private lateinit var dao: FavouritesDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room
            .inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                KinoDatabase::class.java
            ).allowMainThreadQueries()
            .build()
        dao = database.favouritesDao()
    }

    @After
    fun closeDb() = database.close()

    @After
    fun cleanTestDispatcher() = testDispatcher.cleanupTestCoroutines()


    //      ----------      ADD FAVOURITE       ----------------------------------------------------

    @Test
    fun addFavourite_emptyDatabase_favouriteIsAdded() = testDispatcher.runBlockingTest {
        val prevFavourites = dao.getFavouritesIds()
        assertThat(prevFavourites).isNotNull()
        assertThat(prevFavourites).isEmpty()

        dao.addFavourite("123")

        val result = dao.getFavouritesIds()
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(listOf("123"))
    }

    @Test
    fun addFavourite_addingAlreadyLoadedItem_replacePreviousLoadedAndDoesNotAdmitDuplicates() =
        testDispatcher.runBlockingTest {
            dao.addFavourite("123")
            assertThat(dao.getFavouritesIds()).isEqualTo(listOf("123"))

            dao.addFavourite("123")
            val result = dao.getFavouritesIds()
            assertThat(result).isEqualTo(listOf("123"))
            assertThat(result).containsNoDuplicates()
        }


    //      ----------      REMOVE FAVOURITE       -------------------------------------------------

    @Test
    fun removeFavourite_removeIndividualItem_And_RemoveUntilListIsEmpty() =
        testDispatcher.runBlockingTest {
            dao.addFavourite("123")
            dao.addFavourite("456")
            assertThat(dao.getFavouritesIds()).isEqualTo(listOf("123", "456"))

            dao.removeFavourite("456")
            assertThat(dao.getFavouritesIds()).isEqualTo(listOf("123"))

            dao.removeFavourite("123")
            assertThat(dao.getFavouritesIds()).isEmpty()
        }

    @Test
    fun removeFavourite_removeNonExistentFavouriteDoesNothing() = testDispatcher.runBlockingTest {
        dao.addFavourite("123")
        dao.addFavourite("456")
        assertThat(dao.getFavouritesIds()).isEqualTo(listOf("123", "456"))

        dao.removeFavourite("789")
        assertThat(dao.getFavouritesIds()).isEqualTo(listOf("123", "456"))
    }


    //      ----------      OBSERVE FAVOURITES       -----------------------------------------------

    @Test
    fun observeFavouritesIds_favAddedAndRemoved_flowEmitCorrectValues() =
        testDispatcher.runBlockingTest {
            assertThat(dao.getFavouritesIds()).isEmpty()

            val favouritesFlow = dao.observeFavouritesIds()
            assertThat(favouritesFlow.first()).isEmpty()

            dao.addFavourite("123")
            assertThat(favouritesFlow.first()).isEqualTo(listOf("123"))

            dao.removeFavourite("123")
            assertThat(favouritesFlow.first()).isEqualTo(emptyList<String>())
        }


    //      ----------      IS FAVOURITE       ----------------------------------------------------

    @Test
    fun isFavourite_itemLoadedInDatabase_returnTrue() = testDispatcher.runBlockingTest {
        dao.addFavourite("123")
        assertThat(dao.getFavouritesIds()).isEqualTo(listOf("123"))

        val result = dao.isFavourite("123")
        assertThat(result).isTrue()
    }

    @Test
    fun isFavourite_itemNotInDatabase_returnFalse() = testDispatcher.runBlockingTest {
        dao.addFavourite("123")
        assertThat(dao.getFavouritesIds()).isEqualTo(listOf("123"))

        val result = dao.isFavourite("456")
        assertThat(result).isFalse()
    }

    @Test
    fun observeIsFavourite_favAddedAndRemoved_flowEmitCorrectValues() =
        testDispatcher.runBlockingTest {
            dao.addFavourite("123")
            assertThat(dao.getFavouritesIds()).isEqualTo(listOf("123"))

            val isFavouriteFlow = dao.observeIsFavourite("456")
            assertThat(isFavouriteFlow.first()).isFalse()

            dao.addFavourite("456")
            assertThat(isFavouriteFlow.first()).isTrue()

            dao.removeFavourite("456")
            assertThat(isFavouriteFlow.first()).isFalse()
        }

}