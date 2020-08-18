# Foobar++ Android

Foobar++ is a group ordering and delivery application.

## Description

Food ordering and delivery services such as foodpanda serve individual customers. Many restaurants do not deliver to the UST area. This project will create a mobile app that supports group orders to individual vendors.

## Layers

* **App** -  A layer that interacts with the UI. Contains of UI-related elements such as Activities, Fragments, ViewModels. It depends on both data and domain module.

* **Domain** - A layer that contains business logic. It will include all the usecases and abstractions of the applicaiton. It is a separated pure Java module.

* **Data** - A layer that implement the interface exposed by the domain layer and dispenses data to the app layer.

## Libraries Used

* **Activity** - Access composable APIs built on top of Activity.

* **Appcompat** - Allows access to new APIs on older API versions of the platform.

* **Data Binding** - Bind UI components in layouts to data sources using a declarative format.

* **Fragment** - Segment your app into multiple, independent screens that are hosted within an Activity.

* **Hilt** - Extend the functionality of Dagger Hilt to enable dependency injection of certain classes from the androidx libraries.

* **Lifecycle** - Build lifecycle-aware components that can adjust behavior based on the current lifecycle state of an activity or fragment.

* **Material Components** - Provides material-themed UI components.

* **Navigation** - Build and structure your in-app UI, handle deep links, and navigate between screens.

* **Paging** - Load data in pages, and present it in a RecyclerView.

* **Room** - Create, store, and manage persistent data backed by a SQLite database.

* **Work** - Schedule and execute deferrable, constraint-based background tasks.

* **Glide** - Media management and image loading framework.

* **LeakCanary** - Memory leak detection library.

* **Kotlin Coroutines** - Managing asynchronous tasks with simplified codes and reducing needs for callbacks.


## Firebase Libraries

* **Authentication** - Save user data and handle login logics.

* **Firestore** - NoSQL cloud database.

* **Cloud Functions** - Run backend code in response to events triggered by Firebase features and HTTPS requests.

* **Cloud Messaging** - Sends messages to multiple platforms.

* **Cloud Storage** - Stores user-generated contents, such as photos and videos.


## Usage
1. Download the project.

```console
git clone https://github.com/foobar-UST/foobar-android.git
```

2. Open the project with Android Studio.

