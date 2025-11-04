package com.example.bebeka.logik

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient


object Supabase {
    private const val SUPABASE_URL = "https://your-project.supabase.co"
    private const val SUPABASE_KEY = "your-anon-key"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {

    }
}