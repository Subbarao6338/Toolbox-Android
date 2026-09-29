let scripts = {}

const calculator = { mode: 'deg' }

// Evaluate scripts
scripts['evaluateAll'] = function(csv) {
  return csv.split(',').map(function(expression) {
    try {
      return math.evaluate(expression);
    } catch (e) {
      return null;
    }
  }).join(',');
}

// Trigonometry scripts
const basicFunctions = ['sin', 'cos', 'tan', 'sec', 'cot', 'csc']
basicFunctions.forEach(function(name) {
  const fn = math[name]
  const fnNumber = function (x) {
    if (calculator.mode == 'deg') {
      if (name == 'sin') {
        if (x % 180 == 0) return 0;
      }
      if (name == 'cos') {
        if (x % 360 == 0) return 1;
        if (x % 180 == 0) return -1;
        if (x % 90 == 0) return 0;
      }
      if (name == 'tan') {
        if (x % 180 == 0) return 0;
        if (x % 90 == 0) return 1 / 0;
      }
      return fn(x / 360 * 2 * Math.PI);
    } else {
      if (name == 'sin') {
        if (x % Math.PI == 0) return 0;
      }
      if (name == 'tan') {
        if (x % Math.PI == 0) return 0;
      }
      return fn(x);
    }
  }
  const fnComplex = function(x) {
    if (calculator.mode === 'deg') {
      const factor = Math.PI / 180;
      const scaled = math.complex(x.re * factor, x.im * factor);
      return fn(scaled);
    }
    return fn(x);
  }
  scripts[name] = math.typed(name, {
    'number': fnNumber,
    'Complex': fnComplex,
    'Array | Matrix': function (x) {
      return math.map(x, fnNumber)
    }
  })
})

const inverseFunctions = ['asin', 'acos', 'atan', 'atan2', 'acot', 'acsc', 'asec']
inverseFunctions.forEach(function(name) {
  const fn = math[name]
  const fnNumber = function (x) {
    const result = fn(x)
    if (typeof result === 'number') {
      if (calculator.mode == 'deg') {
        return result / 2 / Math.PI * 360;
      } else {
        return result;
      }
    }
    return result
  }
  const fnComplex = function(x) {
    const result = fn(x);
    if (calculator.mode === 'deg') {
      return math.multiply(result, 180 / Math.PI);
    }
    return result;
  }
  scripts[name] = math.typed(name, {
    'number': fnNumber,
    'Complex': fnComplex,
    'Array | Matrix': function (x) {
      return math.map(x, fnNumber)
    }
  })
})

math.import(scripts, {override: true})