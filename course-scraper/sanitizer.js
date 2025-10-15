let courses = require("./courses.json")
const fs = require("fs")

function fixString(str) {
  str = str.toLocaleLowerCase().trim()
  let newStr = ""

  if (str == "null") return null

  for (let i = 0; i < str.length; i++) {
    if (i > 0 && str.charAt(i) != " " && str.charAt(i-1) == "ª") {
      newStr += " "
    }
    if (i == 0 || (str.charAt(i-1) == " " || str.charAt(i-1) == "-")&& str.charAt(i) != " ") {
      newStr += str.charAt(i).toLocaleUpperCase()
    } else {
      if (str.charAt(i) == " " && str.charAt(i-1) == " ") continue;
      newStr += str.charAt(i)
    }
  }
  return newStr
}

function fixDuration(str) {
  let num = 0
  str = str.toLowerCase().trim()
  if (str == "null") num = null
  else if (str.includes("1 ano e meio")) num = 18
  else if (str.includes("anos e meio")) num = Number.parseInt(str.split(" ")[0])*12 + 6
  else if (str.includes("anos")) num = Number.parseInt(str.split(" ")[0])*12
  else if (str.includes("meses")) num = Number.parseInt(str.split(" ")[0])
  else num = null

  return num
}

function fixHours(str) {
  str = str.toLocaleLowerCase().trim().replaceAll(".", "")
  let num = 0
  if (str == "null") num = null
  else num = Number.parseInt(str.split(" ")[0])

  return num;
}

function main() {
  console.log("Sanitizing courses")

  let newCourses = []

  courses.forEach(course => {
    console.log("Sanitizing " + course.name + ".. ")
  
    course.name = fixString(course.name)
    course.qualification = fixString(course.qualification)
    course.duration_months = fixDuration(course.duration_months)
    course.hours = fixHours(course.hours)
    course.degree = fixString(course.degree)
   
  
    newCourses.push(course)
    console.log("Done\n")
  })

  console.log("Writing sanitized json to file..")

  fs.writeFileSync("./sanitized-courses.json", JSON.stringify(newCourses, null, 2))

  console.log("Generating sql queries..")

  let sqlQueries = ""

  newCourses.forEach(course => {
    let keys = "( "
    let values = "( "
    for (let key in course) {
      let value = course[key]
      if (typeof value == "string") value = "'" + value + "'"
      keys += key + ", "
      values += value + ", "
    }
    keys += ")"
    values += ")"

    let query = "INSERT INTO public.course " + keys + " VALUES" + values + ";"
    query = query.replaceAll(", )", " )")
    sqlQueries += query + "\n"
  })

  fs.writeFileSync("./queries.sql", sqlQueries)
}




main()